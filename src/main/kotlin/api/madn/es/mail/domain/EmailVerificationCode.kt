package api.madn.es.mail.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "email_verification_codes")
open class EmailVerificationCode(
    @Column(name = "email", length = 128, nullable = false)
    open var email: String,

    @Column(name = "code", length = 6, nullable = false)
    open var code: String,

    @Column(name = "expires_at", nullable = false)
    open var expiresAt: LocalDateTime
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(name = "verified_at")
    open var verifiedAt: LocalDateTime? = null

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    open var createdAt: LocalDateTime? = null
        protected set

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    open var updatedAt: LocalDateTime? = null
        protected set

    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)

    fun isVerified(): Boolean = verifiedAt != null

    fun markAsVerified() {
        verifiedAt = LocalDateTime.now()
    }
}