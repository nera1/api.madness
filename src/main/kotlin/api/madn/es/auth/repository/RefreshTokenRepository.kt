package api.madn.es.auth.repository

import api.madn.es.auth.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByTokenHash(tokenHash: String): RefreshToken?

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.tokenHash = :tokenHash")
    fun deleteByTokenHash(@Param("tokenHash") tokenHash: String)

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.userId = :userId")
    fun deleteAllByUserId(@Param("userId") userId: Long)

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < CURRENT_TIMESTAMP")
    fun deleteExpiredTokens(): Int
}