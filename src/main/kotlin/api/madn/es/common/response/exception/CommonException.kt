package api.madn.es.common.response.exception

open class CommonException(
    message : String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
}

