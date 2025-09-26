package kr.mdns.madness.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "live-rollup")
@Getter
@Setter
public class LiveRollupConfig {
    private int batchSize = 500;
    private Sql sql;

    @Getter
    @Setter
    public static class Sql {
        private String upsert;
        private String deleteStale = "DELETE FROM channel_live_rollup WHERE observed_at < :observedAt";
    }
}
