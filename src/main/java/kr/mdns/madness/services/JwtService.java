package kr.mdns.madness.services;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.mdns.madness.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtil jwtUtil;

    public String resolveAccessToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = extractBearer(header);
        if (token != null) {
            return token;
        }
        return extractCookie(request.getCookies(), "sess_id");
    }

    public String resolveAccessToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = extractBearer(header);
        if (token != null) {
            return token;
        }

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletReq = ((ServletServerHttpRequest) request).getServletRequest();
            return extractCookie(servletReq.getCookies(), "sess_id");
        }
        return null;
    }

    private String extractBearer(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String extractCookie(Cookie[] cookies, String name) {
        if (cookies == null)
            return null;
        Optional<Cookie> c = Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
        return c.map(Cookie::getValue).orElse(null);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public Long getUserId(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }

    public Instant getExpiry(String token) {
        Date expDate = jwtUtil.getExpirationDate(token);
        return expDate.toInstant();
    }

    public Claims getAllClaims(String token) {
        return jwtUtil.parseClaims(token);
    }
}
