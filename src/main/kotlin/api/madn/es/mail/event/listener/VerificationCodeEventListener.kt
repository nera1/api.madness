package api.madn.es.mail.event.listener

import api.madn.es.mail.event.VerificationCodeSaveEvent
import api.madn.es.mail.service.EmailVerificationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class VerificationCodeEventListener(
    private val emailVerificationService: EmailVerificationService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun onVerificationCodeSave(event: VerificationCodeSaveEvent) {
        try {
            val (email, code) = event
            emailVerificationService.saveEmailVerificationCode(email = email, code = code)
            log.info("Verification code saved for email: ${event.email}")
        } catch (e: Exception) {
            log.error("Failed to save verification code for email: ${event.email}", e)
        }
    }
}