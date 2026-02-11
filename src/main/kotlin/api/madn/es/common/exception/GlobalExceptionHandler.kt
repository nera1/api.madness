package api.madn.es.common.exception

import api.madn.es.auth.exception.EmailDuplicationException
import api.madn.es.common.response.ApiResponse
import api.madn.es.mail.exception.VerificationCodeAlreadyUsedException
import api.madn.es.mail.exception.VerificationCodeExpiredException
import api.madn.es.mail.exception.VerificationCodeMismatchException
import api.madn.es.mail.exception.VerificationCodeNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(EmailDuplicationException::class)
    fun handleEmailDuplication(ex: CommonException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.EMAIL_DUPLICATE.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.EMAIL_DUPLICATE))
    }

    @ExceptionHandler(VerificationCodeNotFoundException::class)
    fun handleNotFound(ex: VerificationCodeNotFoundException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_NOT_FOUND.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_NOT_FOUND))
    }

    @ExceptionHandler(VerificationCodeExpiredException::class)
    fun handleExpired(ex: VerificationCodeExpiredException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_EXPIRED.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_EXPIRED))
    }

    @ExceptionHandler(VerificationCodeAlreadyUsedException::class)
    fun handleAlreadyUsed(ex: VerificationCodeAlreadyUsedException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_ALREADY_USED.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_ALREADY_USED))
    }

    @ExceptionHandler(VerificationCodeMismatchException::class)
    fun handleMismatch(ex: VerificationCodeMismatchException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(ErrorCode.VERIFICATION_CODE_MISMATCH.HttpStatus)
            .body(ApiResponse.failure(ErrorCode.VERIFICATION_CODE_MISMATCH))
    }
}