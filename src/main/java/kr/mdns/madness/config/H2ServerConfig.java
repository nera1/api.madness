package kr.mdns.madness.config;

import java.sql.SQLException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("h2")
public class H2ServerConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(name = "app.h2.tcp.enabled", havingValue = "true")
    public org.h2.tools.Server h2TcpServer() throws SQLException {
        return org.h2.tools.Server.createTcpServer(
                "-tcp",
                "-tcpPort", "9092",
                "-ifNotExists");
    }
}
