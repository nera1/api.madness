package api.madn.es.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenExpiry: Long = 900,        // 15분 (초)
    val refreshTokenExpiry: Long = 604800,    // 7일 (초)
    val cookieDomain: String = ""             // .domain.com
)