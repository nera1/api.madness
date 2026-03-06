package api.madn.es.project.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.LocalDateTime

data class ProjectDetailResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val title: String,
    val slides: List<SlideItem>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    data class SlideItem(
        @JsonSerialize(using = ToStringSerializer::class)
        val id: Long,
        val sortOrder: Int,
        val headline: Headline,
        val body: JsonNode,
    ) {
        data class Headline(
            val text: String,
            val level: String,
        )
    }
}
