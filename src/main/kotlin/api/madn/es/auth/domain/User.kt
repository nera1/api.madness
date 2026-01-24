package api.madn.es.auth.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
@Table(name = "users")
open class User(
    @Column(name = "display_name", length = 16)
    open var displayName: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    open var status: UserStatus = UserStatus.PENDING
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

enum class UserStatus {
    ACTIVE,PENDING,DELETED
}