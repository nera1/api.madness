package api.madn.es.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth.google")
data class OAuthProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val frontendUri: String = "http://localhost:3002",
)
