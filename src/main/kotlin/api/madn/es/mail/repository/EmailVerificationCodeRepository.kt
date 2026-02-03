package api.madn.es.mail.repository

import api.madn.es.mail.domain.EmailVerificationCode
import org.springframework.data.jpa.repository.JpaRepository

interface EmailVerificationCodeRepository : JpaRepository<EmailVerificationCode, Long> {
}