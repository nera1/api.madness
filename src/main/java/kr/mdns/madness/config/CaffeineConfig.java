package kr.mdns.madness.config;

import java.time.Duration;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CaffeineConfig {

        @Bean
        public CacheManager cacheManager() {
                // 1) TTL 적용된 빌더: expireAfterWrite 5분
                Caffeine<Object, Object> ttlBuilder = Caffeine.newBuilder()
                                .maximumSize(10_000)
                                .expireAfterWrite(Duration.ofMinutes(5));

                // 2) 만료기 없는 빌더
                Caffeine<Object, Object> noTtlBuilder = Caffeine.newBuilder()
                                .maximumSize(10_000);

                // 각각의 CaffeineCache 에서 build() 호출
                Cache joinedChannels = new CaffeineCache(
                                "joinedChannels", ttlBuilder.build());
                Cache channelConnectedCount = new CaffeineCache(
                                "channelConnectedCount", ttlBuilder.build());
                Cache topNMemberJoinedChannels = new CaffeineCache(
                                "topNMemberJoinedChannels", noTtlBuilder.build());

                // SimpleCacheManager 에 직접 등록
                SimpleCacheManager manager = new SimpleCacheManager();
                manager.setCaches(
                                List.of(joinedChannels, channelConnectedCount, topNMemberJoinedChannels));
                return manager;
        }
}
