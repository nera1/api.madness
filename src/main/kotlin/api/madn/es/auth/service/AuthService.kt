package api.madn.es.auth.service

import api.madn.es.auth.data.SignInRequest
import api.madn.es.auth.data.SignInResult
import api.madn.es.auth.domain.User
import api.madn.es.auth.domain.UserCredential
import api.madn.es.auth.data.SignUpRequest
import api.madn.es.auth.data.SignupResponse
import api.madn.es.auth.domain.UserStatus
import api.madn.es.auth.exception.EmailDuplicationException
import api.madn.es.auth.exception.EmailNotVerifiedException
import api.madn.es.auth.exception.InvalidCredentialsException
import api.madn.es.auth.repository.UserCredentialRepository
import api.madn.es.auth.repository.UserRepository
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
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val jwtService: JwtService
) {
    private fun emailExists(email: String): Boolean = userCredentialRepo.existsEmail(email) == 1L

    @Transactional
    fun signUp(request: SignUpRequest): SignupResponse {

        val (email, password, displayName) = request

        if (emailExists(email)) throw EmailDuplicationException()

        val user = userRepo.save(User(displayName))
        val hashed = passwordEncoder.encode(password)

        userCredentialRepo.save(UserCredential(user.id!!, email, hashed))

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

    @Transactional(readOnly = true)
    fun signin(request: SignInRequest): SignInResult {
        val (email, password) = request

        val credential = userCredentialRepo.findByEmail(email)
            ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(password, credential.passwordHash)) {
            throw InvalidCredentialsException()
        }

        val user = userRepo.findById(credential.userId)
            .orElseThrow { InvalidCredentialsException() }

        // PENDING 상태면 로그인 불가 (이메일 인증 필요)
        if (user.status != UserStatus.ACTIVE) {
            throw EmailNotVerifiedException()
        }

        val accessToken = jwtService.generateAccessToken(user.id!!, email)
        val refreshToken = jwtService.generateRefreshToken()

        jwtService.saveRefreshToken(user.id!!, refreshToken)

        return SignInResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            email = email,
            displayName = user.displayName ?: ""
        )
    }
}