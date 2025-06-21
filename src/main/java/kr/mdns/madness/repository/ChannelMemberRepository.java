package kr.mdns.madness.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.mdns.madness.domain.ChannelMember;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    boolean existsByChannelIdAndMemberId(Long channelId, Long memberId);
}
