package api.madn.es.config.logging

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.http-jsonl")
data class HttpJsonlProperties(
    val enabled: Boolean = false,
    val dir: String = "./logs/http",
    val requestFile: String = "requests.jsonl",
    val responseFile: String = "responses.jsonl",
    val maxBodyBytes: Int = 1024 * 1024,
    val redactHeaders: List<String> = listOf("authorization", "cookie", "set-cookie"),
    val redactJsonFields: List<String> = listOf("password", "passwd", "secret", "token")
)
