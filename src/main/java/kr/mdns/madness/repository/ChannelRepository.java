package kr.mdns.madness.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
