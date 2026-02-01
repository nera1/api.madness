package api.madn.es.common

import com.fasterxml.jackson.annotation.JsonInclude

data class ApiError(
    var code : String,
    var message: String,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val details : ErrorDetails? = null,
)
