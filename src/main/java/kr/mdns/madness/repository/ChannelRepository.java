package kr.mdns.madness.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.mdns.madness.domain.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findByPublicId(String publicId);
}
