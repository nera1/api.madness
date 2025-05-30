package kr.mdns.madness.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SigninResponseDto {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
}