package kr.mdns.madness.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CaffeineConfig {

    @Bean
    public CaffeineCacheManager caffeManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager("joinedChannels");
        cm.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10000));
        return cm;
    }
}
