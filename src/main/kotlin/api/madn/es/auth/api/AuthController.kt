package api.madn.es.auth.api

import api.madn.es.auth.dto.SignInRequest
import api.madn.es.auth.dto.SignUpRequest
import api.madn.es.common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {
    @PostMapping("/signin")
    fun signIn(@Valid @RequestBody request : SignInRequest): ApiResponse<*> {
        return ApiResponse.Companion.success(request)
    }

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request : SignUpRequest): ApiResponse<*> {
        return ApiResponse.Companion.success(request)
    }

    @GetMapping("/signout")
    fun signOut(): ApiResponse<*> {
        return ApiResponse.Companion.success("signout")
    }
}