package kr.mdns.madness.config;

import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CaffeineConfig {

    @Bean
    public Caffeine<Object, Object> caffeine() {
        return Caffeine.newBuilder()
                .maximumSize(10_000);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        cacheManager.setCacheNames(List.of("joinedChannels", "channelConnectedCount"));
        return cacheManager;
    }
}