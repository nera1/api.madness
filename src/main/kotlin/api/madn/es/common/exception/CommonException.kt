package api.madn.es.common.exception

open class CommonException(
    message : String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
}

