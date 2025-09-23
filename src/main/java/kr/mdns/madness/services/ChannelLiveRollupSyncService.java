package kr.mdns.madness.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        OffsetDateTime now = OffsetDateTime.now();

        LiveRollupConfig.Sql sql = liveRollupConfig.getSql();
        if (sql == null || sql.getUpsert() == null || sql.getUpsert().isBlank()) {
            throw new IllegalStateException("live-rollup.sql.upsert가 설정되지 않았습니다. 프로필별 yml을 확인하세요.");
        }

        if (!snapshot.isEmpty()) {
            List<MapSqlParameterSource> params = new ArrayList<>(snapshot.size());
            snapshot.forEach((publicId, liveCount) -> params.add(
                    new MapSqlParameterSource()
                            .addValue("publicId", publicId)
                            .addValue("liveCount", liveCount)
                            .addValue("observedAt", now)));

            int batchSize = liveRollupConfig.getBatchSize();
            for (int i = 0; i < params.size(); i += batchSize) {
                int end = Math.min(i + batchSize, params.size());
                jdbc.batchUpdate(sql.getUpsert(),
                        params.subList(i, end).toArray(MapSqlParameterSource[]::new));
            }
        }

        jdbc.update(sql.getDeleteStale(),
                new MapSqlParameterSource("observedAt", now));
    }
}
