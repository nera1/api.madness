package api.madn.es.auth.service

import api.madn.es.auth.config.JwtProperties
import api.madn.es.auth.domain.RefreshToken
import api.madn.es.auth.exception.InvalidTokenException
import api.madn.es.auth.exception.RefreshTokenNotFoundException
import api.madn.es.auth.exception.TokenExpiredException
import api.madn.es.auth.repository.RefreshTokenRepository
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateAccessToken(userId: Long, email: String): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpiry * 1000)

        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }

    fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    @Transactional
    fun saveRefreshToken(userId: Long, refreshToken: String) {
        val tokenHash = hashToken(refreshToken)
        val expiresAt = LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpiry)

        val entity = RefreshToken(
            userId = userId,
            tokenHash = tokenHash,
            expiresAt = expiresAt
        )
        refreshTokenRepository.save(entity)
    }

    @Transactional
    fun validateRefreshToken(refreshToken: String): RefreshToken {
        val tokenHash = hashToken(refreshToken)
        val entity = refreshTokenRepository.findByTokenHash(tokenHash)
            ?: throw RefreshTokenNotFoundException()

        if (entity.isExpired()) {
            refreshTokenRepository.delete(entity)
            throw TokenExpiredException()
        }

        return entity
    }

    @Transactional
    fun revokeRefreshToken(refreshToken: String) {
        val tokenHash = hashToken(refreshToken)
        refreshTokenRepository.deleteByTokenHash(tokenHash)
    }

    @Transactional
    fun revokeAllUserTokens(userId: Long) {
        refreshTokenRepository.deleteAllByUserId(userId)
    }

    fun validateAccessToken(token: String): TokenClaims {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body

            return TokenClaims(
                userId = claims.subject.toLong(),
                email = claims["email"] as String
            )
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException()
        } catch (e: JwtException) {
            throw InvalidTokenException()
        }
    }

    fun setTokenCookies(response: HttpServletResponse, accessToken: String, refreshToken: String) {
        response.addHeader(
            "Set-Cookie",
            buildCookieHeader("access_token", accessToken, "/", jwtProperties.accessTokenExpiry.toInt())
        )
        response.addHeader(
            "Set-Cookie",
            buildCookieHeader("refresh_token", refreshToken, "/auth", jwtProperties.refreshTokenExpiry.toInt())
        )
    }

    fun clearTokenCookies(response: HttpServletResponse) {
        response.addHeader("Set-Cookie", buildCookieHeader("access_token", "", "/", 0))
        response.addHeader("Set-Cookie", buildCookieHeader("refresh_token", "", "/auth", 0))
    }

    private fun buildCookieHeader(name: String, value: String, path: String, maxAge: Int): String {
        val sb = StringBuilder("$name=$value; Path=$path; Max-Age=$maxAge; HttpOnly; Secure; SameSite=Lax")
        if (jwtProperties.cookieDomain.isNotBlank()) {
            sb.append("; Domain=${jwtProperties.cookieDomain}")
        }
        return sb.toString()
    }
}

data class TokenClaims(
    val userId: Long,
    val email: String
)