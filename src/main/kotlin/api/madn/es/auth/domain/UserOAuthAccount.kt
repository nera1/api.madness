package api.madn.es.auth.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

enum class OAuthProvider {
    GOOGLE, GITHUB, KAKAO, NAVER
}

@Entity
@Table(name = "user_oauth_accounts")
open class UserOAuthAccount(

    @Column(name = "user_id", nullable = false)
    open var userId: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    open var provider: OAuthProvider = OAuthProvider.GOOGLE,

    @Column(name = "provider_user_id", length = 128, nullable = false)
    open var providerUserId: String = "",

    @Column(name = "email", length = 128)
    open var email: String? = null
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
