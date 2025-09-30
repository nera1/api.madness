package kr.mdns.madness.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import kr.mdns.madness.domain.Channel;
import kr.mdns.madness.projection.ChannelAndCount;
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
    private OffsetDateTime snapAt;
    private int participants;
    private int memberCount;

    public static ChannelDto from(ChannelAndCount c, int participants) {
        return ChannelDto.builder()
                .name(c.getName())
                .publicId(c.getPublicId())
                .createdAt(c.getCreatedAt())
                .participants(c.getLiveCount())
                .memberCount(c.getMemberCount())
                .snapAt(c.getSnapAt())
                .build();
    }

    public static ChannelDto from(Channel c, int participants) {
        return ChannelDto.builder()
                .name(c.getName())
                .publicId(c.getPublicId())
                .createdAt(c.getCreatedAt())
                .participants(participants)
                .build();
    }
}
