package kr.mdns.madness.dto;

import lombok.Builder;

@Builder
public class SignupResponseDto {
    private String email;
    private String nickname;
}
