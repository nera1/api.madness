package api.madn.es.mail.service

import api.madn.es.mail.template.SignupMailTemplate
import api.madn.es.mail.data.SignupMailData
import api.madn.es.mail.renderer.ThymeleafMailTemplateRenderer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sesv2.SesV2Client
import software.amazon.awssdk.services.sesv2.model.Body
import software.amazon.awssdk.services.sesv2.model.Content
import software.amazon.awssdk.services.sesv2.model.Destination
import software.amazon.awssdk.services.sesv2.model.EmailContent
import software.amazon.awssdk.services.sesv2.model.Message
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest

@Service
class SesMailService(
    private val ses: SesV2Client,
    @Value("\${mail.from}") private val from: String,
    private val renderer: ThymeleafMailTemplateRenderer
) {
    fun sendEmail(to: String, subject: String, text: String? = null, html: String? = null) {
        require(text != null || html != null) {
            "Email content is required. Provide either text or HTML."
        }

        require(from.isNotBlank()) {
            "MAIL_FROM environment variable is not configured."
        }

        val bodyBuilder = Body.builder()
        if (text != null) bodyBuilder.text(Content.builder().data(text).charset("UTF-8").build())
        if (html != null) bodyBuilder.html(Content.builder().data(html).charset("UTF-8").build())

        val emailContent = EmailContent.builder()
            .simple(
                Message.builder()
                    .subject(Content.builder().data(subject).charset("UTF-8").build())
                    .body(bodyBuilder.build())
                    .build()
            )
            .build()

        val req = SendEmailRequest.builder()
            .fromEmailAddress(from)
            .destination(Destination.builder().toAddresses(to).build())
            .content(emailContent)
            .build()

        ses.sendEmail(req)
    }

    fun sendSignupMail(displayName : String, to : String) {
        val model = SignupMailData(displayName, to)
        val template = SignupMailTemplate(model)
        sendEmail(
            to = to,
            subject = template.subject,
            html = renderer.render("signup-mail",  template)
        )
    }
}