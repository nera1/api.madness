package api.madn.es.common.response

import java.time.OffsetDateTime

data class ApiResponse<T>(
    val success : Boolean,
    val data : T? = null,
    val error : ApiError? = null,
    val timestamp: OffsetDateTime? = OffsetDateTime.now()
) {
    companion object {
        fun <T> success(data: T? = null): ApiResponse<T> =
            ApiResponse(success = true, data = data)
        fun <T> failure(
            code : String,
            message : String,
            details : ErrorDetails? = null
        ) : ApiResponse<T> = ApiResponse(
            success = false,
            error = ApiError(code, message, details)
        )
    }
}