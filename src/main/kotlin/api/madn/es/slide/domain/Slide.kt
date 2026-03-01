package api.madn.es.slide.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "slides")
open class Slide(
    @Column(name = "user_id", nullable = false)
    open var userId: Long = 0,

    @Column(name = "headline_text", nullable = false, length = 512)
    open var headlineText: String = "",

    @Column(name = "headline_level", nullable = false, length = 4)
    open var headlineLevel: String = "p",

    @Column(name = "body", nullable = false, columnDefinition = "JSON")
    open var body: String = "{}",
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    open var createdAt: LocalDateTime? = null
        protected set

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    open var updatedAt: LocalDateTime? = null
        protected set
}
