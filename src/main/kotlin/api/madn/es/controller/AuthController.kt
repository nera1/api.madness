package api.madn.es.controller

import api.madn.es.common.request.SignInRequest
import api.madn.es.common.request.SignUpRequest
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
    fun signIn(@Valid @RequestBody signInRequest: SignInRequest): ApiResponse<*> {
        return ApiResponse.success(signInRequest)
    }

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody signUpRequest: SignUpRequest): ApiResponse<*> {
        return ApiResponse.success(signUpRequest)
    }

    @GetMapping("/signout")
    fun logout(): String {
        return "logout"
    }
}