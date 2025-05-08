package kr.mdns.madness.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties properties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var registration = registry.addMapping("/**")
                // 공통 설정
                .allowedMethods(properties.getAllowedMethods().toArray(String[]::new))
                .allowedHeaders(properties.getAllowedHeaders().toArray(String[]::new))
                .allowCredentials(properties.isAllowCredentials())
                .maxAge(properties.getMaxAge());

        // dev: 와일드카드 패턴
        if (!properties.getAllowedOriginPatterns().isEmpty()) {
            registration.allowedOriginPatterns(
                    properties.getAllowedOriginPatterns().toArray(String[]::new));
        }
        // prod: 명시된 Origin 리스트
        else {
            registration.allowedOrigins(
                    properties.getAllowedOrigins().toArray(String[]::new));
        }
    }
}
