package kr.mdns.madness.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import kr.mdns.madness.domain.Channel;
import kr.mdns.madness.domain.ChannelMember;
import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.ChannelDto;
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
        public ChannelMember joinChannelByPublicId(String publicId, Long memberId) {
                Channel channel = channelRepository.findByPublicId(publicId)
                                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + publicId));
                return join(channel.getId(), memberId);
        }

        @Transactional
        public ChannelMember join(Long channelId, Long memberId) {
                if (channelMemberRepository.existsByChannelIdAndMemberId(channelId, memberId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT, "이미 참여한 채널입니다.");
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
                return channelMember;
        }

        public List<ChannelDto> searchChannels(
                        String keyword,
                        String cursor,
                        int size,
                        boolean asc) {
                Sort sort = Sort.by("public_id");
                sort = asc ? sort.ascending() : sort.descending();
                Pageable page = PageRequest.of(0, size, sort);

                List<Channel> channels = channelRepository.search(keyword, cursor, asc, page);

                return channels.stream()
                                .map(c -> new ChannelDto(
                                                c.getPublicId(),
                                                c.getName(),
                                                c.getCreatedAt()))
                                .toList();
        }

}
