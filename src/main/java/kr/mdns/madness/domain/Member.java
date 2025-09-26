package kr.mdns.madness.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "유효한 이메일 형식이어야 합니다")
    @NotBlank(message = "이메일은 필수입니다")
    @Size(min = 5, max = 254, message = "이메일은 5자 이상 254자 이하이어야 합니다")
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하입니다")
    @Pattern(regexp = "^[A-Za-z가-힣0-9_-]+$", message = "닉네임은 영문, 한글, 숫자, '-', '_'만 사용할 수 있습니다")
    @Column(nullable = false, unique = true, length = 12)
    private String nickname;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$", message = "비밀번호는 문자, 숫자, 특수문자를 모두 포함해야 합니다")
    @Column(nullable = false, length = 60)
    private String password;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
