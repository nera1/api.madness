package kr.mdns.madness.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.mdns.madness.dto.SigninRequestDto;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException {
        SigninRequestDto dto;
        try {
            dto = new ObjectMapper()
                    .readValue(request.getInputStream(), SigninRequestDto.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid login request format", e);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(),
                dto.getPassword());
        return authManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult)
            throws IOException, ServletException {
        CustomUserDetails user = (CustomUserDetails) authResult.getPrincipal();
        Long userId = Long.valueOf(user.getId());

        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        ResponseCookie atCookie = ResponseCookie.from("sess_id", accessToken)
                .httpOnly(true)
                .secure(true)
                .domain(".madn.es")
                .path("/")
                .maxAge(jwtUtil.getAccessExpMs() / 1000)
                .sameSite("None")
                .build();

        ResponseCookie rtCookie = ResponseCookie.from("sess_rf", refreshToken)
                .httpOnly(true)
                .secure(true)
                .domain(".madn.es")
                .path("/auth/refresh")
                .maxAge(jwtUtil.getRefreshExpSec())
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, atCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, rtCookie.toString());

        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(Map.of("accessToken", accessToken)));
    }
}
