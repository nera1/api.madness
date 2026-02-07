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
            val (email, displayName, code) = event
            val mailData = SignupMailData(
                to = email,
                displayName = displayName,
                code = code
            )
            mailService.sendTemplateEmail(mailData)
        } catch (e: Exception) {
            log.error("Failed to send welcome email to: ${event.email}", e)
        }
    }
}