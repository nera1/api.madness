// src/main/kotlin/api/madn/es/common/exception/ErrorCode.kt
package api.madn.es.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code : String,
    val message: String,
    val httpStatus: HttpStatus
) {
    EMAIL_DUPLICATE("AUTH_001", "Email in use", HttpStatus.CONFLICT),
    USER_CREDENTIAL_NOT_FOUND("AUTH_002", "User credential not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("AUTH_003", "User not found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_VERIFIED("AUTH_004", "Email not verified", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("AUTH_005", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("AUTH_006", "Invalid token", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("AUTH_007", "Token has expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("AUTH_008", "Refresh token not found", HttpStatus.UNAUTHORIZED),

    VERIFICATION_CODE_NOT_FOUND("MAIL_001", "Verification code not found", HttpStatus.NOT_FOUND),
    VERIFICATION_CODE_EXPIRED("MAIL_002", "Verification code has expired", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_ALREADY_USED("MAIL_003", "Verification code already used", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_MISMATCH("MAIL_004", "Verification code does not match", HttpStatus.BAD_REQUEST),
    MAIL_CONFIGURATION_ERROR("MAIL_005", "Mail configuration error", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_CONTENT_REQUIRED("MAIL_006", "Email content is required", HttpStatus.BAD_REQUEST),

    VALIDATION_ERROR("COMMON_001", "Validation failed", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("COMMON_002", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
}