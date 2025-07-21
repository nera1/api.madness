package kr.mdns.madness.interceptor;

import java.time.Instant;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        Map<String, Object> sessionAttrs = accessor.getSessionAttributes();

        if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {

            if (sessionAttrs == null || !sessionAttrs.containsKey("tokenExpiry")) {
                throw new MessagingException("401");
            }

            Instant expiry = (Instant) sessionAttrs.get("tokenExpiry");

            if (expiry != null && Instant.now().isAfter(expiry)) {
                throw new MessagingException("401");
            }
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

}
