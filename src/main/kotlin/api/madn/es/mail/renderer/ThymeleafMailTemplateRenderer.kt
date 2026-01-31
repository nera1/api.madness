package api.madn.es.mail.renderer

import api.madn.es.mail.data.MailData
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale

@Component
class ThymeleafMailTemplateRenderer(
    private val templateEngine: TemplateEngine
) {
    fun render(data : MailData): String {
        val ctx = Context(Locale.KOREA).apply {
        }
        return templateEngine.process("mail/${"signup-mail"}.html", ctx)
    }
}