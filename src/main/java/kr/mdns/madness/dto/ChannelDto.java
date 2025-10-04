package kr.mdns.madness.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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

    private static OffsetDateTime toUTC(Instant i) {
        return i == null ? null : OffsetDateTime.ofInstant(i, ZoneOffset.UTC);
    }

    private static LocalDateTime toUTCLocal(Instant i) {
        return i == null ? null : OffsetDateTime.ofInstant(i, ZoneOffset.UTC).toLocalDateTime();
    }

    public static ChannelDto from(ChannelAndCount c, int participants) {
        return ChannelDto.builder()
                .name(c.getName())
                .publicId(c.getPublicId())
                .createdAt(toUTCLocal(c.getCreatedAt()))
                .snapAt(toUTC(c.getSnapAt()))
                .participants(participants)
                .memberCount(c.getMemberCount())
                .build();
    }

    public static ChannelDto from(Channel c, int participants) {
        return ChannelDto.builder()
                .name(c.getName())
                .publicId(c.getPublicId())
                .createdAt(c.getCreatedAt())
                .snapAt(null)
                .participants(participants)
                .memberCount(c.getMemberCount())
                .build();
    }
}
