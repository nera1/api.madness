package kr.mdns.madness.services;

import org.springframework.stereotype.Service;

import kr.mdns.madness.repository.ChannelMemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import kr.mdns.madness.domain.ChannelMember;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelMemberService {

    private final ChannelMemberRepository channelMemberRepository;

    @Cacheable(cacheNames = "joinedChannels", key = "#userId")
    public Set<String> findJoinedChannelIds(Long userId) {
        return channelMemberRepository
                .findByMemberId(userId)
                .stream()
                .map(ChannelMember::getPublicChannelId)
                .collect(Collectors.toSet());
    }

    @CacheEvict(cacheNames = "joinedChannels", key = "#userId")
    public void evictJoinedChannelIds(Long userId) {

    }
}
