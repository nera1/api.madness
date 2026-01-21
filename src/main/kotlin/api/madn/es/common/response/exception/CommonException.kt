package api.madn.es.common.response.exception

open class CommonException(
    val code : Int,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
}

