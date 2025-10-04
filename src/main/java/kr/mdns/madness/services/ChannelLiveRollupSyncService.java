package kr.mdns.madness.services;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.mdns.madness.config.LiveRollupConfig;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelLiveRollupSyncService {

    private final ChannelConnectionCountService channelConnectionCountService;
    private final NamedParameterJdbcTemplate jdbc;
    private final LiveRollupConfig liveRollupConfig;

    @Transactional
    public void replaceAllSnapshot() {
        Map<String, Integer> snapshot = channelConnectionCountService.snapshotCounts();
        if (snapshot.isEmpty())
            return;

        OffsetDateTime snapAt = OffsetDateTime.now();
        LiveRollupConfig.Sql sqlProps = liveRollupConfig.getSql();

        List<MapSqlParameterSource> params = new ArrayList<>(snapshot.size());
        snapshot.forEach((publicId, liveCount) -> params.add(
                new MapSqlParameterSource()
                        .addValue("publicId", publicId)
                        .addValue("liveCount", liveCount)
                        .addValue("snapAt", snapAt) // ← 여기!
        ));

        int batchSize = Math.max(1, liveRollupConfig.getBatchSize());

        if (sqlProps.getUpsert() != null && !sqlProps.getUpsert().isBlank()) {
            for (int i = 0; i < params.size(); i += batchSize) {
                int end = Math.min(i + batchSize, params.size());
                jdbc.batchUpdate(
                        sqlProps.getUpsert(),
                        params.subList(i, end).toArray(MapSqlParameterSource[]::new));
            }
            return;
        }

        if (sqlProps.getUpsertUpdate() == null || sqlProps.getUpsertUpdate().isBlank()
                || sqlProps.getUpsertInsert() == null || sqlProps.getUpsertInsert().isBlank()) {
            throw new IllegalStateException(
                    "live-rollup.sql.upsert 또는 (upsert-update / upsert-insert) 설정이 필요합니다.");
        }

        for (int i = 0; i < params.size(); i += batchSize) {
            int end = Math.min(i + batchSize, params.size());
            jdbc.batchUpdate(
                    sqlProps.getUpsertUpdate(),
                    params.subList(i, end).toArray(MapSqlParameterSource[]::new));
        }

        for (int i = 0; i < params.size(); i += batchSize) {
            int end = Math.min(i + batchSize, params.size());
            try {
                jdbc.batchUpdate(
                        sqlProps.getUpsertInsert(),
                        params.subList(i, end).toArray(MapSqlParameterSource[]::new));
            } catch (DuplicateKeyException ignore) {

            }
        }
    }

    @Transactional
    public int deleteStaleOlderThanLatestMinus(Duration ttl) {
        OffsetDateTime cutoff = OffsetDateTime.now().minus(ttl);
        return jdbc.update(
                liveRollupConfig.getSql().getDeleteStale(),
                new MapSqlParameterSource("snapAt", cutoff));
    }
}
