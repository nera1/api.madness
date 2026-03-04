package api.madn.es.slide.api

import api.madn.es.auth.service.TokenClaims
import api.madn.es.common.response.ApiResponse
import api.madn.es.slide.data.CreateSlideRequest
import api.madn.es.slide.data.CreateSlideResponse
import api.madn.es.slide.data.SlideResponse
import api.madn.es.slide.data.UpdateSlideRequest
import api.madn.es.slide.exception.UnauthorizedSlideException
import api.madn.es.slide.service.SlideService
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

    @GetMapping("/{id}")
    fun getSlide(@PathVariable id: Long): ApiResponse<SlideResponse> {
        val claims = SecurityContextHolder.getContext().authentication?.principal as? TokenClaims
            ?: throw UnauthorizedSlideException()

        val response = slideService.getSlide(claims.userId, id)
        return ApiResponse.success(response)
    }

    @PatchMapping("/{id}")
    fun updateSlide(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateSlideRequest,
    ): ApiResponse<SlideResponse> {
        val claims = SecurityContextHolder.getContext().authentication?.principal as? TokenClaims
            ?: throw UnauthorizedSlideException()

        val response = slideService.updateSlide(claims.userId, id, request)
        return ApiResponse.success(response)
    }

    @DeleteMapping("/{id}")
    fun deleteSlide(@PathVariable id: Long): ApiResponse<Nothing> {
        val claims = SecurityContextHolder.getContext().authentication?.principal as? TokenClaims
            ?: throw UnauthorizedSlideException()

        slideService.deleteSlide(claims.userId, id)
        return ApiResponse.success(null)
    }
}
