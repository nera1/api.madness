package api.madn.es.slide.repository

import api.madn.es.slide.domain.Slide
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SlideRepository : JpaRepository<Slide, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Slide>

    @Query("SELECT COALESCE(MAX(s.sortOrder), -1) FROM Slide s WHERE s.project.id = :projectId")
    fun findMaxSortOrderByProjectId(projectId: Long): Int
}
