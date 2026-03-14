package api.madn.es.auth.service

import api.madn.es.auth.config.OAuthProperties
import api.madn.es.auth.domain.OAuthProvider
import api.madn.es.auth.domain.User
import api.madn.es.auth.domain.UserOAuthAccount
import api.madn.es.auth.domain.UserStatus
import api.madn.es.auth.repository.UserCredentialRepository
import api.madn.es.auth.repository.UserOAuthAccountRepository
import api.madn.es.auth.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

data class OAuthUserInfo(
    val providerUserId: String,
    val email: String,
    val name: String?,
)

data class OAuthSignInResult(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val displayName: String,
)

@Service
class OAuthService(
    private val oAuthProperties: OAuthProperties,
    private val userRepository: UserRepository,
    private val userOAuthAccountRepository: UserOAuthAccountRepository,
    private val userCredentialRepository: UserCredentialRepository,
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper,
) {
    private val httpClient: HttpClient = HttpClient.newHttpClient()

    fun getGoogleAuthorizationUrl(): String {
        val params = mapOf(
            "client_id" to oAuthProperties.clientId,
            "redirect_uri" to oAuthProperties.redirectUri,
            "response_type" to "code",
            "scope" to "openid email profile",
            "access_type" to "offline",
            "prompt" to "consent",
        )
        val query = params.entries.joinToString("&") { (k, v) ->
            "$k=${URLEncoder.encode(v, Charsets.UTF_8)}"
        }
        return "https://accounts.google.com/o/oauth2/v2/auth?$query"
    }

    @Transactional
    fun handleGoogleCallback(code: String): OAuthSignInResult {
        val tokenResponse = exchangeCodeForTokens(code)
        val accessTokenGoogle = tokenResponse["access_token"] as? String
            ?: throw IllegalStateException("Google access token not found")

        val userInfo = fetchGoogleUserInfo(accessTokenGoogle)
        return findOrCreateUser(userInfo)
    }

    private fun exchangeCodeForTokens(code: String): Map<*, *> {
        val body = mapOf(
            "code" to code,
            "client_id" to oAuthProperties.clientId,
            "client_secret" to oAuthProperties.clientSecret,
            "redirect_uri" to oAuthProperties.redirectUri,
            "grant_type" to "authorization_code",
        )
        val formBody = body.entries.joinToString("&") { (k, v) ->
            "$k=${URLEncoder.encode(v, Charsets.UTF_8)}"
        }

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://oauth2.googleapis.com/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(formBody))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw IllegalStateException("Google token exchange failed: ${response.body()}")
        }
        return objectMapper.readValue(response.body(), Map::class.java)
    }

    private fun fetchGoogleUserInfo(accessToken: String): OAuthUserInfo {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
            .header("Authorization", "Bearer $accessToken")
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw IllegalStateException("Google userinfo request failed: ${response.body()}")
        }

        val data = objectMapper.readValue(response.body(), Map::class.java)
        return OAuthUserInfo(
            providerUserId = data["id"] as String,
            email = data["email"] as String,
            name = data["name"] as? String,
        )
    }

    private fun findOrCreateUser(userInfo: OAuthUserInfo): OAuthSignInResult {
        // 1) 기존 OAuth 계정 확인
        val existingOAuth = userOAuthAccountRepository.findByProviderAndProviderUserId(
            OAuthProvider.GOOGLE, userInfo.providerUserId
        )

        val user: User = if (existingOAuth != null) {
            userRepository.findById(existingOAuth.userId).orElseThrow {
                IllegalStateException("OAuth account exists but user not found")
            }
        } else {
            // 2) 같은 이메일의 credential 계정이 있으면 연결
            val existingCredential = userCredentialRepository.findByEmail(userInfo.email)
            val linkedUser = if (existingCredential != null) {
                userRepository.findById(existingCredential.userId).orElseThrow {
                    IllegalStateException("Credential exists but user not found")
                }
            } else {
                // 3) 새 유저 생성 (OAuth는 이메일 인증 불필요)
                userRepository.save(User(userInfo.name, UserStatus.ACTIVE))
            }

            // OAuth 계정 연결
            userOAuthAccountRepository.save(
                UserOAuthAccount(
                    userId = linkedUser.id!!,
                    provider = OAuthProvider.GOOGLE,
                    providerUserId = userInfo.providerUserId,
                    email = userInfo.email,
                )
            )
            linkedUser
        }

        // JWT 발급
        val accessToken = jwtService.generateAccessToken(user.id!!, userInfo.email)
        val refreshToken = jwtService.generateRefreshToken()
        jwtService.saveRefreshToken(user.id!!, refreshToken)

        return OAuthSignInResult(
            accessToken = accessToken,
            refreshToken = refreshToken,
            email = userInfo.email,
            displayName = user.displayName ?: "",
        )
    }
}
