package api.madn.es.auth.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
open class RefreshToken(
    @Column(name = "user_id", nullable = false)
    open var userId: Long,

    @Column(name = "token_hash", length = 64, nullable = false)
    open var tokenHash: String,

    @Column(name = "expires_at")
    open var expiresAt: LocalDateTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    open var createdAt: LocalDateTime? = null

    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
}