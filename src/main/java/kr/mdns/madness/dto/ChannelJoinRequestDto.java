package kr.mdns.madness.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelJoinRequestDto {
    private String publicChannelId;
    private String password;
}