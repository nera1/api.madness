package kr.mdns.madness.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto {
    private String email;
    private String nickname;
}
