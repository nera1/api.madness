package kr.mdns.madness.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import kr.mdns.madness.interceptor.ChannelConnectionCountInterceptor;
import kr.mdns.madness.interceptor.JwtAuthInterceptor;
import kr.mdns.madness.interceptor.JwtHandShakeInterceptor;
import kr.mdns.madness.interceptor.StompAuthInterceptor;
import lombok.RequiredArgsConstructor;

@Profile("h2")
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfigDev implements WebSocketMessageBrokerConfigurer {

    private final ChannelConnectionCountInterceptor channelConnectionCountInterceptor;
    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final JwtHandShakeInterceptor jwtHandShakeInterceptor;
    private final StompAuthInterceptor stompAuthInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws/chat")
                .addInterceptors(jwtHandShakeInterceptor)
                .setAllowedOriginPatterns("*", "http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthInterceptor, jwtAuthInterceptor, channelConnectionCountInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }
}
