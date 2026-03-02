package api.madn.es.project.api

import api.madn.es.auth.service.TokenClaims
import api.madn.es.common.response.ApiResponse
import api.madn.es.project.data.CreateProjectRequest
import api.madn.es.project.data.ProjectDetailResponse
import api.madn.es.project.data.ProjectResponse
import api.madn.es.project.exception.UnauthorizedProjectException
import api.madn.es.project.service.ProjectService
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val projectService: ProjectService,
) {
    private fun currentUserId(): Long {
        val claims = SecurityContextHolder.getContext().authentication?.principal as? TokenClaims
            ?: throw UnauthorizedProjectException()
        return claims.userId
    }

    @PostMapping
    fun createProject(
        @Valid @RequestBody request: CreateProjectRequest,
    ): ApiResponse<ProjectResponse> {
        val response = projectService.createProject(currentUserId(), request)
        return ApiResponse.success(response)
    }

    @GetMapping
    fun getProjects(): ApiResponse<List<ProjectResponse>> {
        val response = projectService.getProjects(currentUserId())
        return ApiResponse.success(response)
    }

    @GetMapping("/{id}")
    fun getProject(@PathVariable id: Long): ApiResponse<ProjectDetailResponse> {
        val response = projectService.getProject(currentUserId(), id)
        return ApiResponse.success(response)
    }

    @PatchMapping("/{id}")
    fun updateProjectTitle(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateProjectRequest,
    ): ApiResponse<ProjectResponse> {
        val response = projectService.updateProjectTitle(currentUserId(), id, request.title)
        return ApiResponse.success(response)
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: Long): ApiResponse<Nothing> {
        projectService.deleteProject(currentUserId(), id)
        return ApiResponse.success(null)
    }
}
