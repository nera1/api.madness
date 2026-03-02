package api.madn.es.slide.data

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime

data class SlideResponse(
    val id: Long,
    val headline: Headline,
    val body: JsonNode,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    data class Headline(
        val text: String,
        val level: String,
    )
}
