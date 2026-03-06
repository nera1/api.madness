package api.madn.es.slide.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.LocalDateTime

data class SlideResponse(
    @JsonSerialize(using = ToStringSerializer::class)
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
