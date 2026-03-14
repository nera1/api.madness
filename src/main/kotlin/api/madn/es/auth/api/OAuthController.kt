package api.madn.es.auth.api

import api.madn.es.auth.config.OAuthProperties
import api.madn.es.auth.service.JwtService
import api.madn.es.auth.service.OAuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder

@RestController
@RequestMapping("/auth/oauth")
class OAuthController(
    private val oAuthService: OAuthService,
    private val jwtService: JwtService,
    private val oAuthProperties: OAuthProperties,
) {
    @GetMapping("/google")
    fun redirectToGoogle(response: HttpServletResponse) {
        response.sendRedirect(oAuthService.getGoogleAuthorizationUrl())
    }

    @GetMapping("/google/callback")
    fun handleGoogleCallback(
        @RequestParam code: String,
        response: HttpServletResponse,
    ) {
        val result = oAuthService.handleGoogleCallback(code)
        jwtService.setTokenCookies(response, result.accessToken, result.refreshToken)

        val email = URLEncoder.encode(result.email, Charsets.UTF_8)
        val displayName = URLEncoder.encode(result.displayName, Charsets.UTF_8)
        response.sendRedirect("${oAuthProperties.frontendUri}?oauth=success&email=$email&displayName=$displayName")
    }
}
