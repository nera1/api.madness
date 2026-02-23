package api.madn.es.common.exception

import api.madn.es.auth.exception.EmailDuplicationException
import api.madn.es.auth.exception.EmailNotVerifiedException
import api.madn.es.auth.exception.InvalidCredentialsException
import api.madn.es.auth.exception.InvalidTokenException
import api.madn.es.auth.exception.RefreshTokenNotFoundException
import api.madn.es.auth.exception.TokenExpiredException
import api.madn.es.auth.exception.UserCredentialNotFoundException
import api.madn.es.auth.exception.UserNotFoundException
import api.madn.es.common.ValidationErrorDetails
import api.madn.es.common.response.ApiResponse
import api.madn.es.mail.exception.EmailContentRequiredException
import api.madn.es.mail.exception.MailConfigurationException
import api.madn.es.mail.exception.VerificationCodeAlreadyUsedException
import api.madn.es.mail.exception.VerificationCodeExpiredException
import api.madn.es.mail.exception.VerificationCodeMismatchException
import api.madn.es.mail.exception.VerificationCodeNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // ── Auth ──────────────────────────────────────────────────────────────

    @ExceptionHandler(EmailDuplicationException::class)
    fun handleEmailDuplication(ex: EmailDuplicationException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.EMAIL_DUPLICATE.httpStatus)
            .body(ApiResponse.failure(ErrorCode.EMAIL_DUPLICATE))

    @ExceptionHandler(UserCredentialNotFoundException::class)
    fun handleUserCredentialNotFound(ex: UserCredentialNotFoundException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.USER_CREDENTIAL_NOT_FOUND.httpStatus)
            .body(ApiResponse.failure(ErrorCode.USER_CREDENTIAL_NOT_FOUND))

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.USER_NOT_FOUND.httpStatus)
            .body(ApiResponse.failure(ErrorCode.USER_NOT_FOUND))

    @ExceptionHandler(EmailNotVerifiedException::class)
    fun handleEmailNotVerified(ex: EmailNotVerifiedException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.EMAIL_NOT_VERIFIED.httpStatus)
            .body(ApiResponse.failure(ErrorCode.EMAIL_NOT_VERIFIED))

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.INVALID_CREDENTIALS.httpStatus)
            .body(ApiResponse.failure(ErrorCode.INVALID_CREDENTIALS))

    // ── JWT ───────────────────────────────────────────────────────────────

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(ex: InvalidTokenException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.INVALID_TOKEN.httpStatus)
            .body(ApiResponse.failure(ErrorCode.INVALID_TOKEN))

    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpired(ex: TokenExpiredException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.TOKEN_EXPIRED.httpStatus)
            .body(ApiResponse.failure(ErrorCode.TOKEN_EXPIRED))

    @ExceptionHandler(RefreshTokenNotFoundException::class)
    fun handleRefreshTokenNotFound(ex: RefreshTokenNotFoundException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.REFRESH_TOKEN_NOT_FOUND.httpStatus)
            .body(ApiResponse.failure(ErrorCode.REFRESH_TOKEN_NOT_FOUND))

    // ── Mail ──────────────────────────────────────────────────────────────

    @ExceptionHandler(VerificationCodeNotFoundException::class)
    fun handleVerificationCodeNotFound(ex: VerificationCodeNotFoundException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_NOT_FOUND.httpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_NOT_FOUND))

    @ExceptionHandler(VerificationCodeExpiredException::class)
    fun handleVerificationCodeExpired(ex: VerificationCodeExpiredException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_EXPIRED.httpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_EXPIRED))

    @ExceptionHandler(VerificationCodeAlreadyUsedException::class)
    fun handleVerificationCodeAlreadyUsed(ex: VerificationCodeAlreadyUsedException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_ALREADY_USED.httpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_ALREADY_USED))

    @ExceptionHandler(VerificationCodeMismatchException::class)
    fun handleVerificationCodeMismatch(ex: VerificationCodeMismatchException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_MISMATCH.httpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_MISMATCH))

    @ExceptionHandler(MailConfigurationException::class)
    fun handleMailConfiguration(ex: MailConfigurationException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.MAIL_CONFIGURATION_ERROR.httpStatus)
            .body(ApiResponse.failure(ErrorCode.MAIL_CONFIGURATION_ERROR))

    @ExceptionHandler(EmailContentRequiredException::class)
    fun handleEmailContentRequired(ex: EmailContentRequiredException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.EMAIL_CONTENT_REQUIRED.httpStatus)
            .body(ApiResponse.failure(ErrorCode.EMAIL_CONTENT_REQUIRED))

    // ── Common ────────────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val fieldErrors = ex.bindingResult.fieldErrors.map {
            ValidationErrorDetails.FieldError(
                field = it.field,
                message = it.defaultMessage ?: "Invalid value",
            )
        }
        return ResponseEntity
            .status(ErrorCode.VALIDATION_ERROR.httpStatus)
            .body(ApiResponse.failure(ErrorCode.VALIDATION_ERROR, ValidationErrorDetails(fieldErrors)))
    }

    @ExceptionHandler(CommonException::class)
    fun handleCommon(ex: CommonException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.httpStatus)
            .body(ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR))

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.httpStatus)
            .body(ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR))
}
