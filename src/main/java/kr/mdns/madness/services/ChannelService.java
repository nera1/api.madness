package kr.mdns.madness.services;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.mdns.madness.domain.Channel;
import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.ChannelRequestDto;
import kr.mdns.madness.dto.ChannelResponseDto;
import kr.mdns.madness.repository.ChannelRepository;
import kr.mdns.madness.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {
        private final ChannelRepository channelRepository;
        private final MemberRepository memberRepository;

        @Transactional
        public ChannelResponseDto createChannel(ChannelRequestDto req, Long userId) {
                Member creator = memberRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + userId));

                Channel channel = Channel.builder()
                                .name(req.getName())
                                .creatorId(creator.getId())
                                .publicId(UUID.randomUUID().toString())
                                .build();

                Channel saved = channelRepository.save(channel);

                return ChannelResponseDto.builder()
                                .publicId(saved.getPublicId())
                                .name(saved.getName())
                                .creatorNickname(creator.getNickname())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }
}
