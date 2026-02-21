package api.madn.es.auth.filter

import api.madn.es.auth.exception.InvalidTokenException
import api.madn.es.auth.exception.TokenExpiredException
import api.madn.es.auth.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = request.cookies?.find { it.name == "access_token" }?.value

        if (accessToken != null) {
            try {
                val claims = jwtService.validateAccessToken(accessToken)

                val authentication = UsernamePasswordAuthenticationToken(
                    claims,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_USER"))
                )

                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: TokenExpiredException) {
                // 토큰 만료 - 인증 없이 진행
            } catch (e: InvalidTokenException) {
                // 유효하지 않은 토큰 - 인증 없이 진행
            }
        }

        filterChain.doFilter(request, response)
    }
}