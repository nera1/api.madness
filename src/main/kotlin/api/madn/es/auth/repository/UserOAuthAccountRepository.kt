package api.madn.es.auth.repository

import api.madn.es.auth.domain.OAuthProvider
import api.madn.es.auth.domain.UserOAuthAccount
import org.springframework.data.jpa.repository.JpaRepository

interface UserOAuthAccountRepository : JpaRepository<UserOAuthAccount, Long> {

    fun findByProviderAndProviderUserId(provider: OAuthProvider, providerUserId: String): UserOAuthAccount?

    fun findAllByUserId(userId: Long): List<UserOAuthAccount>

    fun findByProviderAndEmail(provider: OAuthProvider, email: String): UserOAuthAccount?
}
