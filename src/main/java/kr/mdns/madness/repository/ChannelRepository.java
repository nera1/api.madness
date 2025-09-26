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
import kr.mdns.madness.projection.ChannelAndCount;

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
                            (:asc = true  AND public_id > :cursor)
                         OR (:asc = false AND public_id < :cursor)
                        )
                    )
                  ORDER BY
                      CASE WHEN :asc = true  THEN public_id END ASC,
                      CASE WHEN :asc = false THEN public_id END DESC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> search(
                  @Param("keyword") String keyword,
                  @Param("cursor") String cursor,
                  @Param("asc") boolean asc,
                  @Param("size") int size);

      // === 기본(ILIKE) 기반: ASC, 첫 페이지
      @Query(value = """
                  SELECT *
                  FROM channels
                  WHERE replace(lower(name), ' ', '')
                        ILIKE CONCAT('%', replace(lower(:kw), ' ', ''), '%')
                  ORDER BY public_id ASC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> searchAscFirst(@Param("kw") String kw, @Param("size") int size);

      // === 기본(ILIKE) 기반: ASC, cursor 이후
      @Query(value = """
                  SELECT *
                  FROM channels
                  WHERE replace(lower(name), ' ', '')
                        ILIKE CONCAT('%', replace(lower(:kw), ' ', ''), '%')
                    AND public_id > :cursor
                  ORDER BY public_id ASC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> searchAscAfter(@Param("kw") String kw,
                  @Param("cursor") String cursor,
                  @Param("size") int size);

      @Query(value = """
                  SELECT *
                  FROM channels
                  WHERE replace(lower(name), ' ', '')
                        ILIKE CONCAT('%', replace(lower(:kw), ' ', ''), '%')
                  ORDER BY public_id DESC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> searchDescFirst(@Param("kw") String kw, @Param("size") int size);

      // === 기본(ILIKE) 기반: DESC, cursor 이전
      @Query(value = """
                  SELECT *
                  FROM channels
                  WHERE replace(lower(name), ' ', '')
                        ILIKE CONCAT('%', replace(lower(:kw), ' ', ''), '%')
                    AND public_id < :cursor
                  ORDER BY public_id DESC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> searchDescBefore(@Param("kw") String kw,
                  @Param("cursor") String cursor,
                  @Param("size") int size);

      // === PGroonga 전용: ASC, 첫 페이지
      @Query(value = """
                  SELECT *
                  FROM channels
                  WHERE replace(lower(name), ' ', '')
                        &@ replace(lower(:kw), ' ', '')
                  ORDER BY public_id ASC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> searchAscFirstPgroonga(@Param("kw") String kw,
                  @Param("size") int size);

      // === PGroonga 전용: ASC, cursor 이후
      @Query(value = """
                  SELECT *
                  FROM channels
                  WHERE replace(lower(name), ' ', '')
                        &@ replace(lower(:kw), ' ', '')
                    AND public_id > :cursor
                  ORDER BY public_id ASC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> searchAscAfterPgroonga(@Param("kw") String kw,
                  @Param("cursor") String cursor,
                  @Param("size") int size);

      // === PGroonga 전용: DESC, 첫 페이지
      @Query(value = """
                  SELECT *
                  FROM channels
                  WHERE replace(lower(name), ' ', '')
                        &@ replace(lower(:kw), ' ', '')
                  ORDER BY public_id DESC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> searchDescFirstPgroonga(@Param("kw") String kw,
                  @Param("size") int size);

      // === PGroonga 전용: DESC, cursor 이전
      @Query(value = """
                  SELECT *
                  FROM channels
                  WHERE replace(lower(name), ' ', '')
                        &@ replace(lower(:kw), ' ', '')
                    AND public_id < :cursor
                  ORDER BY public_id DESC
                  LIMIT :size
                  """, nativeQuery = true)
      List<Channel> searchDescBeforePgroonga(@Param("kw") String kw,
                  @Param("cursor") String cursor,
                  @Param("size") int size);

      // 공통 유틸
      List<Channel> findAllByPublicIdIn(Collection<String> publicIds);

      @Modifying
      @Transactional
      @Query("UPDATE Channel c SET c.memberCount = c.memberCount + 1 WHERE c.publicId = :publicId")
      int incrementMemberCount(@Param("publicId") String publicId);

      @Modifying
      @Transactional
      @Query("UPDATE Channel c SET c.memberCount = c.memberCount - 1 WHERE c.publicId = :publicId AND c.memberCount > 0")
      int decrementMemberCount(@Param("publicId") String publicId);

      @Query(value = """
                  SELECT *
                  FROM channels
                  ORDER BY member_count DESC
                  LIMIT :limit
                  """, nativeQuery = true)
      List<Channel> findTopMemberJoinedChannels(@Param("limit") int limit);
}
