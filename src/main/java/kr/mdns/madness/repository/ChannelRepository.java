package kr.mdns.madness.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.mdns.madness.domain.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findByPublicId(String publicId);

    List<Channel> findByPublicIdLessThanOrderByPublicIdDesc(
            String cursorPublicId, Pageable pageable);

    List<Channel> findAllByOrderByPublicIdDesc(Pageable pageable);

    List<Channel> findByPublicIdGreaterThanOrderByPublicIdAsc(
            String cursorPublicId, Pageable pageable);

    List<Channel> findAllByOrderByPublicIdAsc(Pageable pageable);
}
