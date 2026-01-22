package api.madn.es.common.response

import api.madn.es.common.response.exception.ErrorCode
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.OffsetDateTime

data class ApiResponse<T>(
    val success: Boolean,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val error: ApiError? = null,

    val timestamp: OffsetDateTime = OffsetDateTime.now()
) {
    companion object {
        fun <T> success(data: T? = null): ApiResponse<T> =
            ApiResponse(success = true, data = data)

        fun failure(
            errorCode: ErrorCode,
            details: ErrorDetails? = null
        ): ApiResponse<Nothing> =
            ApiResponse(
                success = false,
                error = ApiError(
                    code = errorCode.code,
                    message = errorCode.message,
                    details = details
                )
            )
    }
}