package api.madn.es.slide.data

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.LocalDateTime

data class CreateSlideResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val headline: Headline,
    val createdAt: LocalDateTime?,
) {
    data class Headline(
        val text: String,
        val level: String,
    )
}
