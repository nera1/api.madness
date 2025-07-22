package kr.mdns.madness.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChannelDto {
    private String publicId;
    private String name;
    private LocalDateTime createdAt;
    private int participants;
    private int memberCount;
}
