package api.madn.es.mail.service

import api.madn.es.mail.domain.EmailVerificationCode
import api.madn.es.mail.repository.EmailVerificationCodeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EmailVerificationService(
    private val emailVerificationCodeRepository: EmailVerificationCodeRepository
) {
    @Transactional
    fun saveEmailVerificationCode(email: String, code : String) {
        val expiresAt = LocalDateTime.now().plusMinutes(10)

        val verificationCode = EmailVerificationCode(
            email = email,
            code = code,
            expiresAt = expiresAt
        )

        emailVerificationCodeRepository.save(verificationCode)
    }

    @Transactional
    fun verifyCode(email: String, code: String): Boolean {
        val verification = emailVerificationCodeRepository
            .findLatestVerificationCodeByEmail(email) ?: return false

        if (verification.isExpired()) return false
        if (verification.isVerified()) return false
        if (verification.code != code) return false

        verification.markAsVerified()
        return true
    }

     fun generateVerificationCode(n : Int = 6) : String =
        CharArray(n) { ('0'.code + (it * 1103515245 + 12345 ushr 16) % 10).toChar() }.joinToString("")
}