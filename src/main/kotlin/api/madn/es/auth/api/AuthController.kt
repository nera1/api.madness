package api.madn.es.auth.api

import api.madn.es.auth.dto.SignInRequest
import api.madn.es.auth.dto.SignUpRequest
import api.madn.es.auth.service.AuthService
import api.madn.es.common.response.ApiResponse
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
) {
    @PostMapping("/signin")
    fun signIn(@Valid @RequestBody request : SignInRequest): ApiResponse<*> {
        return ApiResponse.success(request)
    }

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request : SignUpRequest): ApiResponse<*> {
        return ApiResponse.success(authService.signUp(request))
    }

    @GetMapping("/signout")
    fun signOut(): ApiResponse<*> {
        return ApiResponse.success("signout")
    }
}