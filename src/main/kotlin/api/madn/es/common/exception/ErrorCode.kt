package api.madn.es.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code : String,
    val message: String,
    val HttpStatus: HttpStatus
) {
    EMAIL_DUPLICATE("AUTH_001", "Email in use", HttpStatus.CONFLICT),

    VERIFICATION_CODE_NOT_FOUND("MAIL_001", "Verification code not found", HttpStatus.NOT_FOUND),
    VERIFICATION_CODE_EXPIRED("MAIL_002", "Verification code expired", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_ALREADY_USED("MAIL_003", "Verification code already used", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_MISMATCH("MAIL_004", "Verification code does not match", HttpStatus.BAD_REQUEST)
}