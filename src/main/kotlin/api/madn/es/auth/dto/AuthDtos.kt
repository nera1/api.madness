package api.madn.es.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignInRequest (
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Size(max = 64)
    val password: String
)

data class SignUpRequest(
    @field:Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "invalid email format"
    )
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,64}$",
        message = "password must contain letters and numbers"
    )
    @field:Size(max = 64)
    val password: String,

    @field:NotBlank
    @field:Size(max = 16)
    val displayName: String
)