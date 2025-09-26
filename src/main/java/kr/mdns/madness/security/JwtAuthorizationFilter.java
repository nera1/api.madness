package kr.mdns.madness.security;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationEntryPoint entryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {
        try {
            String token = extractTokenFromCookie(request);
            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                UserDetails user = userDetailsService.loadUserByUserId(userId);
                Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(request, response);

        } catch (ExpiredJwtException eje) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(
                    request, response,
                    new InsufficientAuthenticationException("토큰이 만료되었습니다", eje));
        } catch (JwtException | IllegalArgumentException je) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(
                    request, response,
                    new InsufficientAuthenticationException("유효하지 않은 토큰입니다", je));
        }
    }

    private String extractTokenFromCookie(HttpServletRequest req) {
        if (req.getCookies() == null)
            return null;
        return Arrays.stream(req.getCookies())
                .filter(c -> "sess_id".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
