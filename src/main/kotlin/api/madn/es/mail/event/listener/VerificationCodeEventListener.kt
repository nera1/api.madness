package api.madn.es.mail.event.listener

import api.madn.es.mail.domain.EmailVerificationCode
import api.madn.es.mail.event.VerificationCodeSaveEvent
import api.madn.es.mail.repository.EmailVerificationCodeRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime

@Component
class VerificationCodeEventListener(
    private val emailVerificationCodeRepository: EmailVerificationCodeRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional
    fun onVerificationCodeSave(event: VerificationCodeSaveEvent) {
        try {
            val expiresAt = LocalDateTime.now().plusMinutes(10)
            val verificationCode = EmailVerificationCode(
                email = event.email,
                code = event.code,
                expiresAt = expiresAt
            )
            emailVerificationCodeRepository.save(verificationCode)
            log.info("Verification code saved for email: ${event.email}")
        } catch (e: Exception) {
            log.error("Failed to save verification code for email: ${event.email}", e)
        }
    }
}