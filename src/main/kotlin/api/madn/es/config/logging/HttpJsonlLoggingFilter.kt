package api.madn.es.config.logging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.BufferedWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class HttpJsonlLoggingFilter(
    private val props: HttpJsonlProperties,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val dirPath: Path = Path.of(props.dir).toAbsolutePath().normalize()
    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val reqCounters = ConcurrentHashMap<Path, AtomicLong>()
    private val resCounters = ConcurrentHashMap<Path, AtomicLong>()
    private val counterInitLock = Any()

    init {
        Files.createDirectories(dirPath)
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val reqWrap = ContentCachingRequestWrapper(request)
        val resWrap = ContentCachingResponseWrapper(response)

        try {
            chain.doFilter(reqWrap, resWrap)
        } finally {
            val method = reqWrap.method
            val uri = reqWrap.requestURI
            val qs = reqWrap.queryString?.let { "?$it" } ?: ""
            val uriWithQs = "$uri$qs"

            val status = resWrap.status

            val reqPath = todayReqPath("requests")
            val resPath = todayReqPath("responses")

            val reqCounter = getOrInitCounter(reqPath, reqCounters)
            val resCounter = getOrInitCounter(resPath, resCounters)

            val statusCol = "-->"
            val reqEntry = buildRequestEntry(reqWrap)
            val reqLineNo = appendJsonLine(reqPath, reqEntry, reqCounter)
            log.info("[REQ] {} {} {}  {}:{}", statusCol, method, uriWithQs, reqPath.toUri().toString(), reqLineNo)

            val resEntry = buildResponseEntry(resWrap)
            val resLineNo = appendJsonLine(resPath, resEntry, resCounter)
            log.info("[RES] {} {} {}  {}:{}", status, method, uriWithQs, resPath.toUri().toString(), resLineNo)

            resWrap.copyBodyToResponse()
        }
    }

    private fun todayReqPath(prefix: String): Path {
        val d = LocalDate.now(zoneId).toString()
        return dirPath.resolve("$prefix-$d.jsonl")
    }

    private fun getOrInitCounter(path: Path, map: ConcurrentHashMap<Path, AtomicLong>): AtomicLong {
        map[path]?.let { return it }

        synchronized(counterInitLock) {
            map[path]?.let { return it }

            Files.createDirectories(path.parent)

            val existingLines =
                if (Files.exists(path)) Files.lines(path).use { it.count() } else 0L

            val counter = AtomicLong(existingLines)
            map[path] = counter
            return counter
        }
    }

    private fun buildRequestEntry(req: ContentCachingRequestWrapper): Map<String, Any?> {
        val charset = req.characterEncoding?.let { Charset.forName(it) } ?: Charsets.UTF_8

        val bodyBytes = req.contentAsByteArray
        val body = extractBodyString(bodyBytes, charset, req.contentType)

        val headers = req.headerNames.toList().associateWith { name ->
            val lower = name.lowercase()
            if (props.redactHeaders.any { it.equals(lower, ignoreCase = true) }) "***REDACTED***"
            else req.getHeaders(name).toList()
        }

        return linkedMapOf(
            "body" to redactJsonIfPossible(body),
            "headers" to headers
        )
    }

    private fun buildResponseEntry(res: ContentCachingResponseWrapper): Map<String, Any?> {
        val charset = res.characterEncoding?.let { Charset.forName(it) } ?: Charsets.UTF_8

        val bodyBytes = res.contentAsByteArray
        val body = extractBodyString(bodyBytes, charset, res.contentType)

        val headers = res.headerNames.associateWith { name ->
            val lower = name.lowercase()
            if (props.redactHeaders.any { it.equals(lower, ignoreCase = true) }) "***REDACTED***"
            else res.getHeaders(name).toList()
        }

        return linkedMapOf(
            "body" to redactJsonIfPossible(body),
            "headers" to headers
        )
    }

    private fun appendJsonLine(path: Path, obj: Any, counter: AtomicLong): Long {
        val json = objectMapper.writeValueAsString(obj)
        val lineNo = counter.incrementAndGet()

        Files.newBufferedWriter(
            path,
            Charsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND,
            StandardOpenOption.WRITE
        ).use { bw: BufferedWriter ->
            bw.append(json).append("\n")
        }

        return lineNo
    }

    private fun extractBodyString(bytes: ByteArray, charset: Charset, contentType: String?): String? {
        if (bytes.isEmpty()) return null
        val limited = if (bytes.size > props.maxBodyBytes) bytes.copyOf(props.maxBodyBytes) else bytes
        val raw = limited.toString(charset)

        val mt = contentType?.let { runCatching { MediaType.parseMediaType(it) }.getOrNull() }
        val isTextLike = mt == null ||
                mt.type == "text" ||
                mt.subtype.contains("json", ignoreCase = true) ||
                mt.subtype.contains("xml", ignoreCase = true) ||
                mt.subtype.contains("x-www-form-urlencoded", ignoreCase = true)

        return if (isTextLike) raw else "<${bytes.size} bytes binary>"
    }

    private fun redactJsonIfPossible(body: String?): Any? {
        if (body == null) return null
        val trimmed = body.trim()
        if (!(trimmed.startsWith("{") && trimmed.endsWith("}")) &&
            !(trimmed.startsWith("[") && trimmed.endsWith("]"))
        ) return body

        return try {
            val node = objectMapper.readTree(body)
            props.redactJsonFields.forEach { key ->
                if (node.has(key)) {
                    (node as? com.fasterxml.jackson.databind.node.ObjectNode)?.put(key, "***REDACTED***")
                }
            }
            node
        } catch (_: Exception) {
            body
        }
    }
}
