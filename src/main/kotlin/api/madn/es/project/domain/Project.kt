package api.madn.es.project.domain

import api.madn.es.slide.domain.Slide
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "projects")
open class Project(
    @Column(name = "user_id", nullable = false)
    open var userId: Long = 0,

    @Column(name = "title", nullable = false, length = 255)
    open var title: String = "",
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @OrderBy("sortOrder ASC")
    open var slides: MutableList<Slide> = mutableListOf()
        protected set

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    open var createdAt: LocalDateTime? = null
        protected set

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    open var updatedAt: LocalDateTime? = null
        protected set
}
