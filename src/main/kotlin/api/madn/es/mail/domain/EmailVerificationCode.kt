package api.madn.es.mail.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "email_verification_codes")
open class EmailVerificationCode(
    @Column(name = "email", length = 128, nullable = false)
    open var email: String,

    @Column(name = "code", length = 6, nullable = false)
    open var code: String,

    @Column(name = "expires_at", length = 6)
    open var expiresAt: LocalDateTime,

    @Column(name = "created_at", length = 6)
    open var createdAt: LocalDateTime,

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(name = "verified_at")
    open var verifiedAt: LocalDateTime? = null

    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)

    fun isVerified(): Boolean = verifiedAt != null


}