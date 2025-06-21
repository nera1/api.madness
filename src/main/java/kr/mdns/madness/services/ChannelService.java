package kr.mdns.madness.services;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.mdns.madness.domain.Channel;
import kr.mdns.madness.domain.ChannelMember;
import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.ChannelRequestDto;
import kr.mdns.madness.dto.ChannelResponseDto;
import kr.mdns.madness.repository.ChannelMemberRepository;
import kr.mdns.madness.repository.ChannelRepository;
import kr.mdns.madness.repository.MemberRepository;
import kr.mdns.madness.util.UuidGenerator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {
        private final ChannelRepository channelRepository;
        private final MemberRepository memberRepository;
        private final ChannelMemberRepository channelMemberRepository;
        private final UuidGenerator uuidGenerator;

        @Transactional
        public ChannelResponseDto createChannel(ChannelRequestDto req, Long userId) {
                Member creator = memberRepository.findById(userId)
                                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + userId));

                Channel channel = Channel.builder()
                                .name(req.getName())
                                .creatorId(creator.getId())
                                .publicId(uuidGenerator.generateV7AsString())
                                .build();

                Channel saved = channelRepository.save(channel);

                ChannelMember cm = ChannelMember.builder()
                                .channelId(saved.getId())
                                .memberId(creator.getId())
                                .build();

                channelMemberRepository.save(cm);

                return ChannelResponseDto.builder()
                                .publicId(saved.getPublicId())
                                .name(saved.getName())
                                .creatorNickname(creator.getNickname())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        @Transactional
        public void joinChannelByPublicId(String publicId, Long memberId) {
                Channel channel = channelRepository.findByPublicId(publicId)
                                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + publicId));
                join(channel.getId(), memberId);
        }

        @Transactional
        public void join(Long channelId, Long memberId) {
                if (channelMemberRepository.existsByChannelIdAndMemberId(channelId, memberId)) {
                        throw new IllegalStateException("이미 참여한 채널입니다.");
                }
                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new NoSuchElementException("Member not found: " + memberId));

                Channel channel = channelRepository.findById(channelId)
                                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

                ChannelMember channelMember = ChannelMember.builder()
                                .channelId(channel.getId())
                                .memberId(member.getId())
                                .build();
                channelMemberRepository.save(channelMember);
        }

}
