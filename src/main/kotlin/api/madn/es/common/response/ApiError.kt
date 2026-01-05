package api.madn.es.common.response

data class ApiError(
    var code : String,
    var message: String,
    val details : ErrorDetails? = null,
)
