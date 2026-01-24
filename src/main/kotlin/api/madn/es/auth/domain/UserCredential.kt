package api.madn.es.auth.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_credentials")
open class UserCredential(

    @Column(name = "user_id", nullable = false)
    open var userId: Long = 0,

    @Column(name = "email", length = 128, nullable = false)
    open var email: String = "",

    @Column(name = "password_hash", length = 255, nullable = false)
    open var passwordHash: String = ""
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0
        protected set

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    open var createdAt: LocalDateTime? = null
        protected set

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    open var updatedAt: LocalDateTime? = null
        protected set
}
