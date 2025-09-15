package kr.mdns.madness.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import kr.mdns.madness.interceptor.ChannelConnectionCountInterceptor;
import kr.mdns.madness.interceptor.JwtAuthInterceptor;
import kr.mdns.madness.interceptor.JwtHandShakeInterceptor;
import kr.mdns.madness.interceptor.StompAuthInterceptor;
import kr.mdns.madness.record.SubscriptionKey;
import kr.mdns.madness.services.ChannelConnectionCountService;
import lombok.RequiredArgsConstructor;

@Profile("!h2")
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ATTR_SUBS = "WS_SUBS";

    private final ChannelConnectionCountService channelConnectionCountService;
    private final ChannelConnectionCountInterceptor channelConnectionCountInterceptor;
    private final JwtHandShakeInterceptor jwtHandShakeInterceptor;
    private final StompAuthInterceptor stompAuthInterceptor;
    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws/chat")
                .addInterceptors(jwtHandShakeInterceptor)
                .setAllowedOriginPatterns("https://madn.es")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtAuthInterceptor, stompAuthInterceptor, channelConnectionCountInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                try {
                    Set<SubscriptionKey> subs = (Set<SubscriptionKey>) session.getAttributes().computeIfAbsent(
                            ATTR_SUBS,
                            key -> ConcurrentHashMap.newKeySet());
                    for (SubscriptionKey key : subs) {
                        channelConnectionCountService.removeSubscription(key);
                    }
                } finally {
                    super.afterConnectionClosed(session, status);
                }
            }
        });
    }
}