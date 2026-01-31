package api.madn.es.mail.renderer

import api.madn.es.mail.data.MailTemplateData
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale

@Component
class ThymeleafMailTemplateRenderer(
    private val templateEngine: TemplateEngine,
    private val objectMapper : ObjectMapper
) {
    fun render(data : MailTemplateData): String {
        val ctx = Context(Locale.KOREA).apply {
            @Suppress("UNCHECKED_CAST")
            val variables = objectMapper.convertValue(data, Map::class.java) as Map<String, Any?>
            variables.forEach { (key, value) -> setVariable(key, value) }
        }
        return templateEngine.process("mail/${data.templateName}.html", ctx)
    }
}