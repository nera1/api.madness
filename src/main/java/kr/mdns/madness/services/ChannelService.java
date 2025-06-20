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

                return ChannelResponseDto.builder()
                                .publicId(saved.getPublicId())
                                .name(saved.getName())
                                .creatorNickname(creator.getNickname())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        // @Transactional
        // public void joinChannel(Long channelId, Long memberId) {
        // // 1) 채널 존재 확인
        // Channel channel = channelRepository.findById(channelId)
        // .orElseThrow(() -> new NoSuchElementException("Channel not found: " +
        // channelId));
        // // 2) 멤버 존재 확인
        // Member member = memberRepository.findById(memberId)
        // .orElseThrow(() -> new NoSuchElementException("Member not found: " +
        // memberId));
        // // 3) 중복 가입 방지
        // boolean exists = channelMemberRepository
        // .existsByChannelIdAndMemberId(channelId, memberId);
        // if (exists) {
        // throw new IllegalStateException("이미 채널에 참여한 상태입니다");
        // }
        // // 4) 가입 처리
        // ChannelMember cm = ChannelMember.builder()
        // .channelId(channel.getId())
        // .memberId(member.getId())
        // .build();
        // channelMemberRepository.save(cm);
        // }

}
