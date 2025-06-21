package kr.mdns.madness.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
import kr.mdns.madness.repository.ChannelMemberRepository;
import kr.mdns.madness.repository.ChannelRepository;
import kr.mdns.madness.repository.MemberRepository;
import kr.mdns.madness.util.UuidGenerator;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {
        @Mock
        private MemberRepository memberRepository;

        @Mock
        private ChannelMemberRepository channelMemberRepository;

        @Mock
        private ChannelRepository channelRepository;

        @Mock
        private UuidGenerator uuidGenerator;

        @InjectMocks
        private ChannelService channelService;

        private final Long CREATOR_ID = 1L;
        private final String CREATOR_EMAIL = "test@test.com";
        private final String CREATOR_NICKNAME = "asdfasdf";
        private final Long CHANNEL_ID = 125L;
        private final String CHANNEL_NAME = "테스트중입니다 모두오세요";

        @Test
        @DisplayName("채널 생성 테스트")
        void testChannelCreate_success() {

                Member creator = Member.builder().id(CHANNEL_ID).email(CREATOR_EMAIL).nickname(CREATOR_NICKNAME)
                                .password("PWD").build();
                given(memberRepository.findById(CREATOR_ID)).willReturn(Optional.of(creator));
                given(uuidGenerator.generateV7AsString()).willReturn("TEST-UUID");
                Channel saved = Channel.builder().id(CHANNEL_ID).name(CHANNEL_NAME).creatorId(creator.getId()).build();
                given(channelRepository.save(any(Channel.class))).willReturn(saved);

                // ChannelRequestDto request =
                // ChannelRequestDto.builder().name(CHANNEL_NAME).build();

                then(channelRepository).should().save(any(Channel.class));
                then(channelMemberRepository).should().save(argThat(
                                cm -> cm.getChannelId().equals(CHANNEL_ID) && cm.getMemberId().equals(CREATOR_ID)));
        }

}
