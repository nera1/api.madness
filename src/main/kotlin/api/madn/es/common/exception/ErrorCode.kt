// src/main/kotlin/api/madn/es/common/exception/ErrorCode.kt
package api.madn.es.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val code : String,
    val message: String,
    val HttpStatus: HttpStatus
) {
    EMAIL_DUPLICATE("AUTH_001", "Email in use", HttpStatus.CONFLICT),
    USER_CREDENTIAL_NOT_FOUND("AUTH_002", "", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("AUTH_003", "", HttpStatus.NOT_FOUND),

    VERIFICATION_CODE_NOT_FOUND("MAIL_001", "", HttpStatus.NOT_FOUND),
    VERIFICATION_CODE_EXPIRED("MAIL_002", "", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_ALREADY_USED("MAIL_003", "", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_MISMATCH("MAIL_004", "", HttpStatus.BAD_REQUEST),

    MAIL_CONFIGURATION_ERROR("MAIL_005", "", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_CONTENT_REQUIRED("MAIL_006", "", HttpStatus.BAD_REQUEST),
}