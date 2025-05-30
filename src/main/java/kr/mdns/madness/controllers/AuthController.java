package kr.mdns.madness.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.SigninRequestDto;
import kr.mdns.madness.dto.SigninResponseDto;
import kr.mdns.madness.security.CustomUserDetails;
import kr.mdns.madness.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@RequestMapping("auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody SigninRequestDto dto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(),
                dto.getPassword());
        Authentication authentication = authManager.authenticate(authToken);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());

        Member member = userDetails.getMember();

        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        SigninResponseDto bodyDto = new SigninResponseDto(
                accessToken,
                refreshToken,
                member.getEmail(),
                member.getNickname(),
                member.getCreatedAt());

        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(userId);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
