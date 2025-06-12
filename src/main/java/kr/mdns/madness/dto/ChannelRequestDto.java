package kr.mdns.madness.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelRequestDto {
    @NotBlank(message = "채널 이름은 필수입니다")
    @Size(max = 254, message = "채널 이름은 최대 254자입니다")
    private String name;
}