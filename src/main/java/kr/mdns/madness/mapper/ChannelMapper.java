package kr.mdns.madness.mapper;

import org.springframework.stereotype.Component;

import kr.mdns.madness.domain.Channel;
import kr.mdns.madness.dto.ChannelDto;
import kr.mdns.madness.services.ChannelConnectionCountService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final ChannelConnectionCountService channelConnectionCountService;

    public ChannelDto toDto(Channel c) {
        return ChannelDto.builder()
                .name(c.getName())
                .publicId(c.getPublicId())
                .createdAt(c.getCreatedAt())
                .participants(channelConnectionCountService.getUserCount(c.getPublicId()))
                .build();
    }
}
