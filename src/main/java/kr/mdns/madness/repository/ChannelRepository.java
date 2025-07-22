package kr.mdns.madness.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import kr.mdns.madness.domain.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findByPublicId(String publicId);

    @Query(value = """
            SELECT *
            FROM channels
            WHERE REPLACE(LOWER(name), ' ', '')
                  LIKE CONCAT('%', REPLACE(LOWER(:keyword), ' ', ''), '%')
              AND (
                  :cursor IS NULL
                  OR (
                      :asc = true AND public_id > :cursor
                      OR :asc = false AND public_id < :cursor
                  )
              )
            ORDER BY
                CASE WHEN :asc = true THEN public_id END ASC,
                CASE WHEN :asc = false THEN public_id END DESC
            LIMIT :size
            """, nativeQuery = true)
    List<Channel> search(
            @Param("keyword") String keyword,
            @Param("cursor") String cursor,
            @Param("asc") boolean asc,
            @Param("size") int size);

    List<Channel> findAllByPublicIdIn(Collection<String> publicIds);

    @Modifying
    @Transactional
    @Query("UPDATE Channel c SET c.memberCount = c.memberCount + 1 WHERE c.publicId = :publicId")
    int incrementMemberCount(@Param("publicId") String publicId);

    @Modifying
    @Transactional
    @Query("UPDATE Channel c SET c.memberCount = c.memberCount - 1 WHERE c.publicId = :publicId AND c.memberCount > 0")
    int decrementMemberCount(@Param("publicId") String publicId);

    @Query(value = "SELECT * " +
            "FROM channels " +
            "ORDER BY member_count DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Channel> findTopMemberJoinedChannels(@Param("limit") int limit);

}
