package kr.mdns.madness.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelResponseDto {
    private String publicId;
    private String name;
    private String creatorNickname;
    private LocalDateTime createdAt;
}
