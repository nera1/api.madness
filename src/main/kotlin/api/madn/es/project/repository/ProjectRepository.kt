package api.madn.es.project.repository

import api.madn.es.project.domain.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<Project, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Project>
}
