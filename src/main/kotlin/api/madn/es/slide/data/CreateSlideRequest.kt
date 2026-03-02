package api.madn.es.slide.data

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class CreateSlideRequest(
    val projectId: Long? = null,

    @field:Valid
    @field:NotNull
    val headline: Headline,

    @field:NotNull
    val body: Any,
) {
    data class Headline(
        val text: String = "",
        val level: String = "p",
    )
}
