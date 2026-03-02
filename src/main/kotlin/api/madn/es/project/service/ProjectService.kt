package api.madn.es.project.service

import api.madn.es.project.data.CreateProjectRequest
import api.madn.es.project.data.ProjectDetailResponse
import api.madn.es.project.data.ProjectResponse
import api.madn.es.project.domain.Project
import api.madn.es.project.exception.ProjectAccessDeniedException
import api.madn.es.project.exception.ProjectNotFoundException
import api.madn.es.project.repository.ProjectRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun createProject(userId: Long, request: CreateProjectRequest): ProjectResponse {
        val project = Project(
            userId = userId,
            title = request.title,
        )

        val saved = projectRepository.save(project)

        return ProjectResponse(
            id = saved.id!!,
            title = saved.title,
            slideCount = 0,
            createdAt = saved.createdAt,
            updatedAt = saved.updatedAt,
        )
    }

    @Transactional(readOnly = true)
    fun getProjects(userId: Long): List<ProjectResponse> {
        return projectRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
            .map { project ->
                ProjectResponse(
                    id = project.id!!,
                    title = project.title,
                    slideCount = project.slides.size,
                    createdAt = project.createdAt,
                    updatedAt = project.updatedAt,
                )
            }
    }

    @Transactional(readOnly = true)
    fun getProject(userId: Long, projectId: Long): ProjectDetailResponse {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ProjectNotFoundException() }

        if (project.userId != userId) {
            throw ProjectAccessDeniedException()
        }

        return ProjectDetailResponse(
            id = project.id!!,
            title = project.title,
            slides = project.slides.map { slide ->
                ProjectDetailResponse.SlideItem(
                    id = slide.id!!,
                    sortOrder = slide.sortOrder,
                    headline = ProjectDetailResponse.SlideItem.Headline(
                        text = slide.headlineText,
                        level = slide.headlineLevel,
                    ),
                    body = objectMapper.readTree(slide.body),
                )
            },
            createdAt = project.createdAt,
            updatedAt = project.updatedAt,
        )
    }

    @Transactional
    fun updateProjectTitle(userId: Long, projectId: Long, title: String): ProjectResponse {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ProjectNotFoundException() }

        if (project.userId != userId) {
            throw ProjectAccessDeniedException()
        }

        project.title = title
        val saved = projectRepository.save(project)

        return ProjectResponse(
            id = saved.id!!,
            title = saved.title,
            slideCount = saved.slides.size,
            createdAt = saved.createdAt,
            updatedAt = saved.updatedAt,
        )
    }

    @Transactional
    fun deleteProject(userId: Long, projectId: Long) {
        val project = projectRepository.findById(projectId)
            .orElseThrow { ProjectNotFoundException() }

        if (project.userId != userId) {
            throw ProjectAccessDeniedException()
        }

        projectRepository.delete(project)
    }
}
