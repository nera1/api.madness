package api.madn.es.slide.api

import api.madn.es.auth.service.TokenClaims
import api.madn.es.common.response.ApiResponse
import api.madn.es.slide.data.CreateSlideRequest
import api.madn.es.slide.data.CreateSlideResponse
import api.madn.es.slide.exception.UnauthorizedSlideException
import api.madn.es.slide.service.SlideService
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/slides")
class SlideController(
    private val slideService: SlideService,
) {
    @PostMapping
    fun createSlide(
        @Valid @RequestBody request: CreateSlideRequest,
    ): ApiResponse<CreateSlideResponse> {
        val claims = SecurityContextHolder.getContext().authentication?.principal as? TokenClaims
            ?: throw UnauthorizedSlideException()

        val response = slideService.createSlide(claims.userId, request)
        return ApiResponse.success(response)
    }
}
