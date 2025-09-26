package kr.mdns.madness.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SigninRequestDto {
    public String email;
    public String password;
}
