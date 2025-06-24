package kr.mdns.madness.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelDto {
    private String publicId;
    private String name;
    private LocalDateTime createdAt;
}
