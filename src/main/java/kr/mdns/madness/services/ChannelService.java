package kr.mdns.madness.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
import kr.mdns.madness.projection.ChannelAndCount;
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
                channelRepository.incrementMemberCount(saved.getPublicId());
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
                channelRepository.incrementMemberCount(publicChannelId);
                channelMemberService.evictJoinedChannelIds(memberId);

                return channelMember;
        }

        @Transactional
        public boolean leave(String publicChannelId, Long memberId) {
                long deleted = channelMemberRepository
                                .deleteByPublicChannelIdAndMemberId(publicChannelId, memberId);

                if (deleted > 0) {
                        channelRepository.decrementMemberCount(publicChannelId);
                }

                channelMemberService.evictJoinedChannelIds(memberId);

                return deleted > 0;
        }

        public List<Channel> searchNameOrderBy(String keyword, String cursor, boolean asc, int size, Integer count) {
                if (asc) {
                        if (cursor == null) {
                                return channelRepository.searchAscFirst(keyword, size);
                        } else {
                                return channelRepository.searchAscAfter(keyword, cursor, size);
                        }
                } else {
                        if (cursor == null) {
                                return channelRepository.searchDescFirst(keyword, size);
                        } else {
                                return channelRepository.searchDescBefore(keyword, cursor, size);
                        }
                }
        }

        @Transactional(readOnly = true)
        public List<ChannelAndCount> searchByLiveCount(String keyword,
                        OffsetDateTime snapAt,
                        Integer cursorLiveCount,
                        String cursorPublicId,
                        int size,
                        boolean asc) {
                boolean hasCursor = cursorLiveCount != null && cursorPublicId != null;

                if (!hasCursor) {
                        return asc
                                        ? channelRepository.searchByLiveAscFirst(keyword, size)
                                        : channelRepository.searchByLiveDescFirst(keyword, size);
                }

                if (snapAt == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "snapAt is required when cursor is present");
                }

                return asc
                                ? channelRepository.searchByLiveAscAfter(keyword, snapAt, cursorLiveCount,
                                                cursorPublicId, size)
                                : channelRepository.searchByLiveDescAfter(keyword, snapAt, cursorLiveCount,
                                                cursorPublicId, size);
        }

        public List<ChannelDto> searchChannels(String keyword, String cursor, int size, boolean asc, Integer count,
                        OffsetDateTime snapAt) {

                List<Channel> channels = new ArrayList<>();
                System.out.println("===================");
                if (snapAt == null) {
                        System.out.println("No snap, HERE = " + snapAt + "COUNT = " + count);
                        // channels = searchNameOrderBy(keyword, cursor, asc, size, count);
                } else {
                        System.out.println("Snap, HERE = " + snapAt + "COUNT = " + count);
                        // channels = searchNameOrderBy(keyword, cursor, asc, size, count);
                }
                System.out.println("===================");

                channels = searchNameOrderBy(keyword, cursor, asc, size, count);

                return channels.stream()
                                .map(c -> ChannelDto.from(c,
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

        public List<ChannelDto> getTopNParticipantChannels(int topN) {
                List<String> topPublicIds = channelConnectionCountService.getTopNParticipantChannels(topN);

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

        public int incrementMemberCount(String publicId) {
                return channelRepository.incrementMemberCount(publicId);
        }

        public int decrementMemberCount(String publicId) {
                return channelRepository.decrementMemberCount(publicId);
        }

        @Cacheable(value = "topNMemberJoinedChannels", key = "#topN")
        @Transactional(readOnly = true)
        public List<ChannelDto> getTopMemberJoinedChannels(int topN) {
                return findTopMemberJoinedChannels(topN);
        }

        @CachePut(value = "topNMemberJoinedChannels", key = "#topN")
        @Transactional(readOnly = true)
        public List<ChannelDto> refreshTopMemberJoinedChannels(int topN) {
                return findTopMemberJoinedChannels(topN);
        }

        private List<ChannelDto> findTopMemberJoinedChannels(int topN) {
                List<Channel> channels = channelRepository.findTopMemberJoinedChannels(topN);
                return channels.stream()
                                .map(c -> ChannelDto.builder()
                                                .publicId(c.getPublicId())
                                                .name(c.getName())
                                                .createdAt(c.getCreatedAt())
                                                .participants(channelConnectionCountService
                                                                .getUserCount(c.getPublicId()))
                                                .memberCount(c.getMemberCount())
                                                .build())
                                .collect(Collectors.toList());
        }
}
