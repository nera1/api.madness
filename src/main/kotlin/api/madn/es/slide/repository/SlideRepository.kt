package api.madn.es.slide.repository

import api.madn.es.slide.domain.Slide
import org.springframework.data.jpa.repository.JpaRepository

interface SlideRepository : JpaRepository<Slide, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Slide>
}
