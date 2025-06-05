package kr.mdns.madness.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.AuthMeResponseDto;
import kr.mdns.madness.dto.SigninResponseDto;
import kr.mdns.madness.repository.MemberRepository;
import kr.mdns.madness.response.ApiResponse;
import kr.mdns.madness.security.CustomUserDetails;
import kr.mdns.madness.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
        private final JwtUtil jwtUtil;
        private final MemberRepository memberRepository;

        @Value("${app.cookie.secure}")
        private boolean cookieSecure;

        @Value("${app.cookie.sameSite}")
        private String cookieSameSite;

        @Value("${app.cookie.domain}")
        private String cookieDomain;

        @PostMapping("/signin")
        public ResponseEntity<ApiResponse<SigninResponseDto>> signin(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                Long userId = userDetails.getId();
                Member member = userDetails.getMember();
                String accessTok = jwtUtil.generateAccessToken(userId);
                String refreshTok = jwtUtil.generateRefreshToken(userId);
                String domainOrNull = cookieDomain.isBlank() ? null : cookieDomain;

                ResponseCookie atCookie = ResponseCookie.from("sess_id", accessTok)
                                .httpOnly(true)
                                .secure(cookieSecure)
                                .domain(domainOrNull)
                                .path("/")
                                .maxAge(jwtUtil.getAccessExpMs() / 1000)
                                .sameSite(cookieSameSite)
                                .build();

                ResponseCookie rtCookie = ResponseCookie.from("sess_rf", refreshTok)
                                .httpOnly(true)
                                .secure(cookieSecure)
                                .domain(domainOrNull)
                                .path("/")
                                .maxAge(jwtUtil.getRefreshExpSec())
                                .sameSite(cookieSameSite)
                                .build();

                SigninResponseDto payload = new SigninResponseDto(
                                member.getEmail(),
                                member.getNickname(),
                                member.getCreatedAt(),
                                member.getUpdatedAt());
                ApiResponse<SigninResponseDto> body = new ApiResponse<>(0, "sign in", payload);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, atCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(body);
        }

        @GetMapping("/refresh")
        public ResponseEntity<ApiResponse<SigninResponseDto>> refresh(
                        @CookieValue(name = "sess_rf", required = false) String refreshToken) {

                if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new ApiResponse<>(401, "Invalid or missing refresh token", null));
                }

                Long userId = jwtUtil.getUserIdFromToken(refreshToken);
                Optional<Member> memberOpt = memberRepository.findById(userId);
                if (memberOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new ApiResponse<>(401, "User not found", null));
                }

                String newAccessTok = jwtUtil.generateAccessToken(userId);
                String newRefreshTok = jwtUtil.generateRefreshToken(userId);

                String domainOrNull = cookieDomain.isBlank() ? null : cookieDomain;

                ResponseCookie atCookie = ResponseCookie.from("sess_id", newAccessTok)
                                .httpOnly(true)
                                .secure(cookieSecure)
                                .domain(domainOrNull)
                                .path("/")
                                .maxAge(jwtUtil.getAccessExpMs() / 1000)
                                .sameSite(cookieSameSite)
                                .build();

                ResponseCookie rtCookie = ResponseCookie.from("sess_rf", newRefreshTok)
                                .httpOnly(true)
                                .secure(cookieSecure)
                                .domain(domainOrNull)
                                .path("/")
                                .maxAge(jwtUtil.getRefreshExpSec())
                                .sameSite(cookieSameSite)
                                .build();

                ApiResponse<SigninResponseDto> body = new ApiResponse<>(0, "token refreshed", null);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, atCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(body);
        }

        @GetMapping("/me")
        public ResponseEntity<ApiResponse<AuthMeResponseDto>> me(
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                if (userDetails == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new ApiResponse<>(401, "unauthorized", null));
                }

                Member member = userDetails.getMember();
                AuthMeResponseDto payload = new AuthMeResponseDto(
                                member.getEmail(),
                                member.getNickname(),
                                member.getCreatedAt(),
                                member.getUpdatedAt());

                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(new ApiResponse<>(0, "ok", payload));
        }

        @PostMapping("/signout")
        public ResponseEntity<ApiResponse<Object>> signout() {
                String domainOrNull = cookieDomain.isBlank() ? null : cookieDomain;

                ResponseCookie atCookie = ResponseCookie.from("sess_id", "")
                                .httpOnly(true)
                                .secure(cookieSecure)
                                .domain(domainOrNull)
                                .path("/")
                                .maxAge(0)
                                .sameSite(cookieSameSite)
                                .build();

                ResponseCookie rtCookie = ResponseCookie.from("sess_rf", "")
                                .httpOnly(true)
                                .secure(cookieSecure)
                                .domain(domainOrNull)
                                .path("/")
                                .maxAge(0)
                                .sameSite(cookieSameSite)
                                .build();

                ApiResponse<Object> body = new ApiResponse<>(0, "logged out", null);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, atCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(body);
        }
}
