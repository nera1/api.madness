package api.madn.es.project.data

import jakarta.validation.constraints.NotBlank

data class CreateProjectRequest(
    @field:NotBlank
    val title: String = "",
)
