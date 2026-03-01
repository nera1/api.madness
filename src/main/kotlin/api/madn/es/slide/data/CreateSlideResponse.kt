package api.madn.es.slide.data

import java.time.LocalDateTime

data class CreateSlideResponse(
    val id: Long,
    val headline: Headline,
    val createdAt: LocalDateTime?,
) {
    data class Headline(
        val text: String,
        val level: String,
    )
}
