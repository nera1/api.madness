package api.madn.es.auth.service

import api.madn.es.auth.data.SignInRequest
import api.madn.es.auth.domain.User
import api.madn.es.auth.domain.UserCredential
import api.madn.es.auth.data.SignUpRequest
import api.madn.es.auth.data.SignupResponse
import api.madn.es.auth.exception.EmailDuplicationException
import api.madn.es.auth.repository.UserCredentialRepository
import api.madn.es.auth.repository.UserRepository
import api.madn.es.common.profile.ProfileExecutor
import api.madn.es.mail.domain.EmailVerificationCode
import api.madn.es.mail.event.EmailVerificationRequestedEvent
import api.madn.es.mail.event.VerificationCodeSaveEvent
import api.madn.es.mail.service.EmailVerificationService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userCredentialRepo: UserCredentialRepository,
    private val userRepo: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailVerificationService: EmailVerificationService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private fun emailExists(email: String): Boolean = userCredentialRepo.existsEmailQuery(email) == 1L

    @Transactional
    fun signUp(request: SignUpRequest): SignupResponse {

        val (email, password, displayName) = request

        if (emailExists(email)) throw EmailDuplicationException()

        val user = userRepo.save(User(displayName))
        val hashed = passwordEncoder.encode(password)

        ProfileExecutor.execute {
            onProduction = {
                userCredentialRepo.save(UserCredential(user.id!!, email, hashed))
            }
            onDev = {
                devLog("save user credential id: ${user.id} email: $email hashed: $hashed")
            }
        }

        val verificationCode = emailVerificationService.generateVerificationCode(6)

        applicationEventPublisher.publishEvent(
            VerificationCodeSaveEvent(
                email,
                verificationCode
            )
        )

        applicationEventPublisher.publishEvent(
            EmailVerificationRequestedEvent(
                email = email,
                displayName = displayName,
                code = verificationCode
            )
        )

        return SignupResponse(user.id!!, email, displayName)
    }

    @Transactional
    fun signin(request: SignInRequest): Unit {

    }
}