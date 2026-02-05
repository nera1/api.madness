package api.madn.es.mail.event.listener

import api.madn.es.mail.event.*
import api.madn.es.mail.data.SignupMailData
import api.madn.es.mail.service.SesMailService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AuthMailEventListener(
    private val mailService: SesMailService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun onEmailVerificationRequested(event: EmailVerificationRequestedEvent) {
        try {
            val mailData = SignupMailData(
                displayName = event.displayName,
                to = event.email
            )
            mailService.sendTemplateEmail(mailData)
            log.info("Signup welcome email sent to: ${event.email}")
        } catch (e: Exception) {
            log.error("Failed to send welcome email to: ${event.email}", e)
        }
    }
}