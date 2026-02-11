package api.madn.es.mail.service

import api.madn.es.auth.domain.UserStatus
import api.madn.es.auth.repository.UserCredentialRepository
import api.madn.es.auth.repository.UserRepository
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
import kotlin.random.Random

@Service
class EmailVerificationService(
    private val emailVerificationCodeRepository: EmailVerificationCodeRepository,
    private val userCredentialRepository: UserCredentialRepository,
    private val userRepository: UserRepository,
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
    fun verifyCode(email: String, code: String) {
        val verification = emailVerificationCodeRepository
            .findLatestByEmail(email)
            ?: throw VerificationCodeNotFoundException()

        if (verification.isExpired()) throw VerificationCodeExpiredException()
        if (verification.isVerified()) throw VerificationCodeAlreadyUsedException()
        if (verification.code != code) throw VerificationCodeMismatchException()

        verification.markAsVerified()

        val credential = userCredentialRepository.findByEmail(email) ?: throw IllegalStateException("Credential not found")
        val user = userRepository.findById(credential.userId).orElseThrow { IllegalStateException("User not found") }
        user.status = UserStatus.ACTIVE
    }

    fun generateVerificationCode(n: Int = 6): String {
        return buildString {
            repeat(n) {
                append(Random.nextInt(0, 10))
            }
        }
    }
}