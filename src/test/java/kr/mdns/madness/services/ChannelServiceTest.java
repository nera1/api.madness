package kr.mdns.madness.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.mdns.madness.repository.ChannelRepository;
import kr.mdns.madness.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class ChannelServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChannelRepository channelRepository;
}
