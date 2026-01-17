package api.madn.es.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignInRequest (
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Size(max = 64)
    val password: String
)

data class SignUpRequest(
    @field:Email(message = "invalid email format")
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,64}$",
        message = "password must contain letters and numbers"
    )
    @field:Size(max = 64)
    val password: String
)