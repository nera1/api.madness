package kr.mdns.madness.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.mdns.madness.domain.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

        Optional<Channel> findByPublicId(String publicId);

        @Query(value = """
                        SELECT *
                          FROM channel
                         WHERE replace(lower(name), ' ', '')
                               LIKE concat('%', replace(lower(:kw), ' ', ''), '%')
                           AND (
                               :cursor IS NULL
                               OR (
                                   (:asc = true  AND public_id > :cursor)
                                OR (:asc = false AND public_id < :cursor)
                               )
                           )
                         ORDER BY public_id
                        """, nativeQuery = true)
        List<Channel> search(
                        @Param("keyword") String keyword,
                        @Param("cursor") String cursor,
                        @Param("asc") boolean asc,
                        Pageable pageable);

}
