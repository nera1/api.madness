package kr.mdns.madness.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.mdns.madness.domain.Channel;
import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.ChannelRequestDto;
import kr.mdns.madness.dto.ChannelResponseDto;
import kr.mdns.madness.repository.ChannelRepository;
import kr.mdns.madness.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    private final Long CREATOR_ID = 1L;
    private final String CREATOR_EMAIL = "test@test.com";
    private final Long CHANNEL_ID = 125L;
    private final String CHANNEL_NAME = "테스트중입니다 모두오세요";

    @Test
    @DisplayName("채널 생성 테스트")
    void testChannelCreate_success() {
        Member mockCreator = Member.builder()
                .id(CREATOR_ID)
                .email(CREATOR_EMAIL)
                .build();
        when(memberRepository.findById(CREATOR_ID)).thenReturn(Optional.of(mockCreator));

        Channel unsaved = Channel.builder()
                .name(CHANNEL_NAME)
                .creator(mockCreator)
                .build();

        Channel saved = Channel.builder()
                .id(CHANNEL_ID)
                .name(CHANNEL_NAME)
                .creator(mockCreator)
                .createdAt(unsaved.getCreatedAt())
                .build();

        when(channelRepository.save(any(Channel.class))).thenReturn(saved);

        ChannelRequestDto req = ChannelRequestDto.builder().name(CHANNEL_NAME).build();

        ChannelResponseDto resp = channelService.createChannel(req, CREATOR_ID);

        assertThat(resp.getId()).isEqualTo(CHANNEL_ID);
        assertThat(resp.getName()).isEqualTo(CHANNEL_NAME);
        assertThat(resp.getCreatorId()).isEqualTo(CREATOR_ID);
        assertThat(resp.getCreatedAt()).isEqualTo(unsaved.getCreatedAt());

        verify(memberRepository).findById(CREATOR_ID);
        verify(channelRepository).save(any(Channel.class));
    }
}
