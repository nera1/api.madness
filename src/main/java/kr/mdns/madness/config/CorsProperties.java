package kr.mdns.madness.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
public class CorsProperties {
    private List<String> allowedOrigins = new ArrayList<>();

    private List<String> allowedOriginPatterns = new ArrayList<>();

    private List<String> allowedMethods;

    private List<String> allowedHeaders;

    private boolean allowCredentials;

    private String redirectUrl;

    private long maxAge;
}
