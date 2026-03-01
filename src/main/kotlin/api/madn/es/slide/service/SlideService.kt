package api.madn.es.slide.service

import api.madn.es.slide.data.CreateSlideRequest
import api.madn.es.slide.data.CreateSlideResponse
import api.madn.es.slide.domain.Slide
import api.madn.es.slide.repository.SlideRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SlideService(
    private val slideRepository: SlideRepository,
    private val objectMapper: ObjectMapper,
) {
    @Transactional
    fun createSlide(userId: Long, request: CreateSlideRequest): CreateSlideResponse {
        val slide = Slide(
            userId = userId,
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
}
