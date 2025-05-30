package kr.mdns.madness.controllers;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.SigninResponseDto;
import kr.mdns.madness.response.ApiResponse;
import kr.mdns.madness.security.CustomUserDetails;
import kr.mdns.madness.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@RequestMapping("auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
        private final JwtUtil jwtUtil;

        @PostMapping("/signin")
        public ResponseEntity<ApiResponse<SigninResponseDto>> signin(
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        HttpServletResponse response) {

                Long userId = userDetails.getId();
                Member member = userDetails.getMember();
                String accessTok = jwtUtil.generateAccessToken(userId);
                String refreshTok = jwtUtil.generateRefreshToken(userId);

                ResponseCookie atCookie = ResponseCookie.from("sess_id", accessTok)
                                .httpOnly(true).secure(true).domain(".madn.es")
                                .path("/").maxAge(jwtUtil.getAccessExpMs() / 1000).sameSite("None").build();
                ResponseCookie rtCookie = ResponseCookie.from("sess_rf", refreshTok)
                                .httpOnly(true).secure(true).domain(".madn.es")
                                .path("/").maxAge(jwtUtil.getRefreshExpSec()).sameSite("None").build();

                SigninResponseDto payload = new SigninResponseDto(
                                member.getEmail(),
                                member.getNickname(),
                                member.getCreatedAt(), member.getUpdatedAt());
                ApiResponse<SigninResponseDto> body = new ApiResponse<>(0, "sign in", payload);

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, atCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(body);
        }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
                String refreshToken = body.get("sess_rf");

                if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
                }

                Long userId = jwtUtil.getUserIdFromToken(refreshToken);
                String newAccessToken = jwtUtil.generateAccessToken(userId);

                return ResponseEntity.ok(Map.of("sess_id", newAccessToken));
        }
}
