package api.madn.es.controller

import api.madn.es.common.request.SignInRequest
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
    fun login(@Valid @RequestBody request : SignInRequest): ApiResponse<*> {
        return ApiResponse.success(request)
    }

    @PostMapping("/signup")
    fun register(@Valid @RequestBody request : SignInRequest): ApiResponse<*> {
        return ApiResponse.success(request)
    }

    @GetMapping("/signout")
    fun logout(): ApiResponse<*> {
        return ApiResponse.success("signout")
    }
}