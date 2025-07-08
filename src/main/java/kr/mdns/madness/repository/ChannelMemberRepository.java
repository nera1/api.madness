package kr.mdns.madness.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.mdns.madness.domain.ChannelMember;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    boolean existsByPublicChannelIdAndMemberId(String publicChannelId, Long memberId);

    List<ChannelMember> findByMemberId(Long memberId);
}
