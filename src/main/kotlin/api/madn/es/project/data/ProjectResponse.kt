package api.madn.es.project.data

import java.time.LocalDateTime

data class ProjectResponse(
    val id: Long,
    val title: String,
    val slideCount: Int,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
