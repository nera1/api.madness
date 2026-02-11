// src/main/kotlin/api/madn/es/common/exception/GlobalExceptionHandler.kt
package api.madn.es.common.exception

import api.madn.es.auth.exception.EmailDuplicationException
import api.madn.es.auth.exception.UserCredentialNotFoundException
import api.madn.es.auth.exception.UserNotFoundException
import api.madn.es.common.response.ApiResponse
import api.madn.es.mail.exception.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(EmailDuplicationException::class)
    fun handleEmailDuplication(ex: EmailDuplicationException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.EMAIL_DUPLICATE.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.EMAIL_DUPLICATE))
    }

    @ExceptionHandler(UserCredentialNotFoundException::class)
    fun handleUserCredentialNotFound(ex: UserCredentialNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.USER_CREDENTIAL_NOT_FOUND.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.USER_CREDENTIAL_NOT_FOUND))
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.USER_NOT_FOUND.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.USER_NOT_FOUND))
    }

    @ExceptionHandler(VerificationCodeNotFoundException::class)
    fun handleVerificationCodeNotFound(ex: VerificationCodeNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_NOT_FOUND.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_NOT_FOUND))
    }

    @ExceptionHandler(VerificationCodeExpiredException::class)
    fun handleVerificationCodeExpired(ex: VerificationCodeExpiredException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_EXPIRED.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_EXPIRED))
    }

    @ExceptionHandler(VerificationCodeAlreadyUsedException::class)
    fun handleVerificationCodeAlreadyUsed(ex: VerificationCodeAlreadyUsedException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_ALREADY_USED.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_ALREADY_USED))
    }

    @ExceptionHandler(VerificationCodeMismatchException::class)
    fun handleVerificationCodeMismatch(ex: VerificationCodeMismatchException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_MISMATCH.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_MISMATCH))
    }

    @ExceptionHandler(EmailContentRequiredException::class)
    fun handleEmailContentRequired(ex: EmailContentRequiredException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.EMAIL_CONTENT_REQUIRED.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.EMAIL_CONTENT_REQUIRED))
    }

    @ExceptionHandler(MailConfigurationException::class)
    fun handleMailConfiguration(ex: MailConfigurationException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.MAIL_CONFIGURATION_ERROR.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.MAIL_CONFIGURATION_ERROR))
    }
}