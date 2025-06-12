package kr.mdns.madness.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.mdns.madness.domain.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
