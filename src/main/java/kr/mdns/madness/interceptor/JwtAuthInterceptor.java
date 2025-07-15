package kr.mdns.madness.interceptor;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
        if (sessionAttrs != null && sessionAttrs.containsKey("auth")) {
            Authentication auth = (Authentication) sessionAttrs.get("auth");
            // 3) auth 객체 정보 로그 찍어보기
            System.out.println("[ChannelInterceptor] auth.principal = " + auth.getPrincipal());
            System.out.println("[ChannelInterceptor] auth.principal = " + auth.getAuthorities());
        } else {
            System.out.println("[ChannelInterceptor] sessionAttributes에 auth가 없습니다.");
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }
}
