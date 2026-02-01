package api.madn.es.auth.service

import api.madn.es.auth.data.SignInRequest
import api.madn.es.auth.domain.User
import api.madn.es.auth.domain.UserCredential
import api.madn.es.auth.data.SignUpRequest
import api.madn.es.auth.data.SignupResponse
import api.madn.es.auth.exception.EmailDuplicationException
import api.madn.es.auth.repository.UserCredentialRepository
import api.madn.es.auth.repository.UserRepository
import api.madn.es.mail.data.SignupMailData
import api.madn.es.mail.event.EmailVerificationRequestedEvent
import api.madn.es.mail.service.SesMailService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userCredentialRepo: UserCredentialRepository,
    private val userRepo: UserRepository,
    private val passwordEncoder : PasswordEncoder,
    private val mailService: SesMailService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private fun emailExists(email: String) : Boolean = userCredentialRepo.existsEmailQuery(email) == 1L

    @Transactional
    fun signUp(request: SignUpRequest): SignupResponse {
        if (emailExists(request.email)) throw EmailDuplicationException()

        val user = userRepo.save(User(request.displayName))
        val hashed = passwordEncoder.encode(request.password)
        userCredentialRepo.save(UserCredential(user.id!!, request.email, hashed))

        applicationEventPublisher.publishEvent(
            EmailVerificationRequestedEvent(
                email = request.email,
                displayName = request.displayName
            )
        )

        return SignupResponse(user.id!!, request.email, request.displayName)
    }

    @Transactional
    fun signin(request: SignInRequest) : Unit {

    }
}