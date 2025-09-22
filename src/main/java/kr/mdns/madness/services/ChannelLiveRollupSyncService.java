package kr.mdns.madness.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelLiveRollupSyncService {

    private static final int BATCH_SIZE = 500;

    private final ChannelConnectionCountService channelConnectionCountService;
    private final NamedParameterJdbcTemplate jdbc;

    private static final String UPSERT_SQL = """
            INSERT INTO channel_live_rollup (public_id, live_count, observed_at)
            VALUES (:publicId, :liveCount, :observedAt)
            ON CONFLICT (public_id) DO UPDATE
            SET live_count = EXCLUDED.live_count,
                observed_at = EXCLUDED.observed_at
            """;

    private static final String DELETE_STALE_SQL = """
            DELETE FROM channel_live_rollup
            WHERE observed_at < :observedAt
            """;

    @Transactional
    public void replaceAllSnapshot() {
        Map<String, Integer> snapshot = channelConnectionCountService.snapshotCounts();
        OffsetDateTime now = OffsetDateTime.now();

        if (!snapshot.isEmpty()) {
            List<MapSqlParameterSource> params = new ArrayList<>(snapshot.size());
            snapshot.forEach((publicId, liveCount) -> params.add(
                    new MapSqlParameterSource()
                            .addValue("publicId", publicId)
                            .addValue("liveCount", liveCount)
                            .addValue("observedAt", now)));
            for (int i = 0; i < params.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, params.size());
                jdbc.batchUpdate(UPSERT_SQL, params.subList(i, end).toArray(MapSqlParameterSource[]::new));
            }
        }

        jdbc.update(DELETE_STALE_SQL, new MapSqlParameterSource("observedAt", now));
    }
}
