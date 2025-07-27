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

import kr.mdns.madness.record.SubscriptionKey;

@Configuration
@EnableCaching
public class CaffeineConfig {

        @Bean
        public CacheManager cacheManager() {
                Caffeine<Object, Object> baseBuilder = Caffeine.newBuilder()
                                .maximumSize(10_000);

                Cache joinedChannels = new CaffeineCache(
                                "joinedChannels",
                                baseBuilder.build());
                Cache topNMemberJoinedChannels = new CaffeineCache(
                                "topNMemberJoinedChannels",
                                baseBuilder.build());

                SimpleCacheManager manager = new SimpleCacheManager();
                manager.setCaches(List.of(joinedChannels, topNMemberJoinedChannels));
                return manager;
        }

        @Bean
        public com.github.benmanes.caffeine.cache.Cache<SubscriptionKey, Boolean> subscriptionCache() {
                return Caffeine.newBuilder()
                                .maximumSize(100_000)
                                .expireAfterWrite(Duration.ofMinutes(2))
                                .build();
        }
}
