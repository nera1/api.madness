package api.madn.es.common.request

import jakarta.validation.constraints.NotBlank

data class SignUpRequest(
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val password: String
)