package api.madn.es.auth.api

import api.madn.es.auth.data.SignInRequest
import api.madn.es.auth.data.SignInResponse
import api.madn.es.auth.data.SignUpRequest
import api.madn.es.auth.data.VerifyEmailRequest
import api.madn.es.auth.service.AuthService
import api.madn.es.auth.service.JwtService
import api.madn.es.common.response.ApiResponse
import api.madn.es.mail.service.EmailVerificationService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val emailVerificationService: EmailVerificationService,
    private val jwtService: JwtService,
) {
    @PostMapping("/signin")
    fun signIn(
        @Valid @RequestBody request: SignInRequest,
        response: HttpServletResponse,
    ): ApiResponse<SignInResponse> {
        val result = authService.signIn(request)
        jwtService.setTokenCookies(response, result.accessToken, result.refreshToken)
        return ApiResponse.success(SignInResponse(email = result.email, displayName = result.displayName))
    }

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request: SignUpRequest): ApiResponse<*> {
        val user = authService.signUp(request)
        return ApiResponse.success(user)
    }

    @GetMapping("/signout")
    fun signOut(): ApiResponse<*> {
        return ApiResponse.success("signout")
    }

    @PostMapping("/email/verification")
    fun verifyEmail(@Valid @RequestBody request: VerifyEmailRequest): ApiResponse<*> {
        emailVerificationService.verifyCode(request.email, request.code)
        return ApiResponse.success(request.email)
    }
}