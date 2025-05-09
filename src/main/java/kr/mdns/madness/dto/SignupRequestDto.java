package kr.mdns.madness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    @Email(message = "유효한 이메일 형식이어야 합니다")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하입니다")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.{8,32}$)(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$", message = "비밀번호는 8~32자, 문자·숫자·특수문자 조합이어야 합니다")
    private String password;
}
