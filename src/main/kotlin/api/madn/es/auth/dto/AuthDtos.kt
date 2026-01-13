package api.madn.es.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
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
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Size(max = 64)
    val password: String
)