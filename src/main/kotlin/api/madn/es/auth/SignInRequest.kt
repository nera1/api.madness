package api.madn.es.auth

import jakarta.validation.constraints.NotBlank

data class SignInRequest (
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val password: String
)