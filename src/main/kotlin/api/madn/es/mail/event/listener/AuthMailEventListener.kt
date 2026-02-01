package api.madn.es.mail.event.listener

import api.madn.es.mail.event.*
import api.madn.es.mail.data.SignupMailData
import api.madn.es.mail.service.SesMailService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AuthMailEventListener(
    private val mailService: SesMailService
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun onEmailVerificationRequested(event: EmailVerificationRequestedEvent) {
        val mailData = SignupMailData(
            displayName = event.displayName,
            to = event.email
        )
        mailService.sendTemplateEmail(mailData)
    }
}