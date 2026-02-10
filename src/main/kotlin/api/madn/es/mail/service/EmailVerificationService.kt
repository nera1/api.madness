package api.madn.es.mail.service

import api.madn.es.common.profile.ProfileExecutor
import api.madn.es.mail.domain.EmailVerificationCode
import api.madn.es.mail.exception.VerificationCodeAlreadyUsedException
import api.madn.es.mail.exception.VerificationCodeExpiredException
import api.madn.es.mail.exception.VerificationCodeMismatchException
import api.madn.es.mail.exception.VerificationCodeNotFoundException
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
        
        ProfileExecutor.execute {
            onDev = {
                devLog("saving verification code: $code")
            }
        }
    }

    @Transactional
    fun verifyCode(email: String, code: String) {
        val verification = emailVerificationCodeRepository
            .findLatestByEmail(email)
            ?: throw VerificationCodeNotFoundException()

        if (verification.isExpired()) throw VerificationCodeExpiredException()
        if (verification.isVerified()) throw VerificationCodeAlreadyUsedException()
        if (verification.code != code) throw VerificationCodeMismatchException()

        verification.markAsVerified()
    }

     fun generateVerificationCode(n : Int = 6) : String =
        CharArray(n) { ('0'.code + (it * 1103515245 + 12345 ushr 16) % 10).toChar() }.joinToString("")
}