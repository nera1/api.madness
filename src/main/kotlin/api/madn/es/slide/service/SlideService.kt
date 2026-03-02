package api.madn.es.slide.service

import api.madn.es.project.exception.ProjectAccessDeniedException
import api.madn.es.project.exception.ProjectNotFoundException
import api.madn.es.project.repository.ProjectRepository
import api.madn.es.slide.data.CreateSlideRequest
import api.madn.es.slide.data.CreateSlideResponse
import api.madn.es.slide.data.SlideResponse
import api.madn.es.slide.domain.Slide
import api.madn.es.slide.exception.SlideAccessDeniedException
import api.madn.es.slide.exception.SlideNotFoundException
import api.madn.es.slide.repository.SlideRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SlideService(
    private val slideRepository: SlideRepository,
    private val projectRepository: ProjectRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun createSlide(userId: Long, request: CreateSlideRequest): CreateSlideResponse {
        val project = request.projectId?.let { projectId ->
            val p = projectRepository.findById(projectId)
                .orElseThrow { ProjectNotFoundException() }
            if (p.userId != userId) {
                throw ProjectAccessDeniedException()
            }
            p
        }

        val sortOrder = if (project != null) {
            slideRepository.findMaxSortOrderByProjectId(project.id!!) + 1
        } else {
            0
        }

        val slide = Slide(
            userId = userId,
            project = project,
            sortOrder = sortOrder,
            headlineText = request.headline.text,
            headlineLevel = request.headline.level,
            body = objectMapper.writeValueAsString(request.body),
        )

        val saved = slideRepository.save(slide)

        return CreateSlideResponse(
            id = saved.id!!,
            headline = CreateSlideResponse.Headline(
                text = saved.headlineText,
                level = saved.headlineLevel,
            ),
            createdAt = saved.createdAt,
        )
    }

    @Transactional(readOnly = true)
    fun getSlide(userId: Long, slideId: Long): SlideResponse {
        val slide = slideRepository.findById(slideId)
            .orElseThrow { SlideNotFoundException() }

        if (slide.userId != userId) {
            throw SlideAccessDeniedException()
        }

        return SlideResponse(
            id = slide.id!!,
            headline = SlideResponse.Headline(
                text = slide.headlineText,
                level = slide.headlineLevel,
            ),
            body = objectMapper.readTree(slide.body),
            createdAt = slide.createdAt,
            updatedAt = slide.updatedAt,
        )
    }
}
