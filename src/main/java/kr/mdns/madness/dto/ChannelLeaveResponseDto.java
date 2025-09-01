package kr.mdns.madness.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelLeaveResponseDto {
    private String publicChannelId;
    private LocalDateTime joinAt;
}