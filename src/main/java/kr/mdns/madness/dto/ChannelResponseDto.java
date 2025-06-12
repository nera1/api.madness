package kr.mdns.madness.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelResponseDto {
    private Long id;
    private String name;
    private Long creatorId;
    private LocalDateTime createdAt;
}
