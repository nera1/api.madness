package api.madn.es.config.logging

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(HttpJsonlProperties::class)
class HttpJsonlLoggingConfig {

    @Bean
    @ConditionalOnProperty(prefix = "app.http-jsonl", name = ["enabled"], havingValue = "true")
    fun httpJsonlLoggingFilter(props: HttpJsonlProperties): HttpJsonlLoggingFilter {
        return HttpJsonlLoggingFilter(props)
    }
}
