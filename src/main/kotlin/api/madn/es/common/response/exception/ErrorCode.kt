package api.madn.es.common.response.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code : String,
    val message: String,
    val HttpStatus: HttpStatus
) {
    EAMIL_DUPLICATIE("AUTH_001", "Email in use", HttpStatus.CONFLICT)
}