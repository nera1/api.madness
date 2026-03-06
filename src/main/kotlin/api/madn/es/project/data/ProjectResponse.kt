package api.madn.es.project.data

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.LocalDateTime

data class ProjectResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val title: String,
    val slideCount: Int,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
