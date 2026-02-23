package api.madn.es.common

sealed interface ErrorDetails

data class ValidationErrorDetails(
    val fields: List<FieldError>,
) : ErrorDetails {
    data class FieldError(
        val field: String,
        val message: String,
    )
}
