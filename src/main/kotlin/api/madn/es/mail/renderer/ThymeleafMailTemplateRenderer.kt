package api.madn.es.mail.renderer

import api.madn.es.mail.data.MailTemplate
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale

@Component
class ThymeleafMailTemplateRenderer(
    private val templateEngine: TemplateEngine
) {
    fun render(templateName: String, template: MailTemplate): String {
        val ctx = Context(Locale.KOREA).apply {
            setVariable("model", template.model)
            setVariable("subject", template.subject)
        }
        return templateEngine.process("mail/${templateName}.html", ctx)
    }
}