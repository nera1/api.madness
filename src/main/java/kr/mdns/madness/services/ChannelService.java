package kr.mdns.madness.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

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
        private final ChannelMemberService channelMemberService;
        private final ChannelConnectionCountService channelConnectionCountService;
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
                                .publicChannelId(saved.getPublicId())
                                .memberId(creator.getId())
                                .build();

                channelMemberRepository.save(cm);
                channelMemberService.evictJoinedChannelIds(userId);

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
                return join(channel.getPublicId(), memberId);
        }

        @Transactional
        public ChannelMember join(String publicChannelId, Long memberId) {
                if (channelMemberRepository.existsByPublicChannelIdAndMemberId(publicChannelId, memberId)) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT, "이미 참여한 채널입니다.");
                }
                Member member = memberRepository.findById(memberId)
                                .orElseThrow(() -> new NoSuchElementException("Member not found: " + memberId));

                Channel channel = channelRepository.findByPublicId(publicChannelId)
                                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + publicChannelId));

                ChannelMember channelMember = ChannelMember.builder()
                                .publicChannelId(channel.getPublicId())
                                .memberId(member.getId())
                                .build();
                channelMemberRepository.save(channelMember);

                channelMemberService.evictJoinedChannelIds(memberId);

                return channelMember;
        }

        public List<ChannelDto> searchChannels(String keyword, String cursor, int size, boolean asc) {
                List<Channel> channels = channelRepository.search(keyword, cursor, asc, size);

                return channels.stream()
                                .map(c -> new ChannelDto(c.getPublicId(), c.getName(), c.getCreatedAt(),
                                                channelConnectionCountService.getUserCount(c.getPublicId())))
                                .collect(Collectors.toList());
        }

        public void checkMembership(String publicId, Long memberId) {
                Channel channel = channelRepository.findByPublicId(publicId)
                                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + publicId));

                boolean joined = channelMemberRepository
                                .existsByPublicChannelIdAndMemberId(channel.getPublicId(), memberId);

                if (!joined) {
                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "채널에 가입되어 있지 않습니다.");
                }
        }

        public boolean isMemberJoined(String publicId, Long memberId) {
                Channel channel = channelRepository.findByPublicId(publicId)
                                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + publicId));
                return channelMemberRepository
                                .existsByPublicChannelIdAndMemberId(channel.getPublicId(), memberId);
        }

        public ChannelResponseDto getChannel(String publicId, Long memberId) {
                checkMembership(publicId, memberId);

                Channel channel = channelRepository.findByPublicId(publicId)
                                .orElseThrow(() -> new NoSuchElementException("Channel not found: " + publicId));

                Member creator = memberRepository.findById(channel.getCreatorId())
                                .orElseThrow(() -> new NoSuchElementException(
                                                "Member not found: " + channel.getCreatorId()));

                return ChannelResponseDto.builder()
                                .publicId(channel.getPublicId())
                                .name(channel.getName())
                                .creatorNickname(creator.getNickname())
                                .createdAt(channel.getCreatedAt())
                                .build();
        }

        public List<ChannelDto> getTopChannels(int topN) {
                List<String> topPublicIds = channelConnectionCountService.getTopChannels(topN);

                if (topPublicIds.isEmpty()) {
                        return Collections.emptyList();
                }

                List<Channel> channels = channelRepository.findAllByPublicIdIn(topPublicIds);

                Map<String, Channel> channelMap = channels.stream()
                                .collect(Collectors.toMap(Channel::getPublicId, channel -> channel));

                return topPublicIds.stream()
                                .map(channelMap::get)
                                .filter(Objects::nonNull)
                                .map(ch -> ChannelDto.builder().publicId(ch.getPublicId()).name(ch.getName())
                                                .createdAt(ch.getCreatedAt())
                                                .participants(channelConnectionCountService
                                                                .getUserCount(ch.getPublicId()))
                                                .build())
                                .collect(Collectors.toList());
        }

}
