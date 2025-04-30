package kr.mdns.madness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String confirmPassword;
}
