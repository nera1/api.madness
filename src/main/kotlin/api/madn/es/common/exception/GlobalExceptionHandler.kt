package api.madn.es.common.exception

import api.madn.es.auth.exception.EmailDuplicationException
import api.madn.es.common.response.ApiResponse
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
}