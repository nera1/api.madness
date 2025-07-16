package kr.mdns.madness.interceptor;

import java.time.Instant;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
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
                return buildErrorFrame(
                        "인증 정보가 없습니다. 다시 로그인해주세요.",
                        "token_expired",
                        accessor);
            }

            Instant expiry = (Instant) sessionAttrs.get("tokenExpiry");

            if (expiry != null && Instant.now().isAfter(expiry)) {
                return buildErrorFrame(
                        "인증 정보가 없습니다. 다시 로그인해주세요.",
                        "token_expired",
                        accessor);
            }
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private Message<byte[]> buildErrorFrame(String userMsg, String code, StompHeaderAccessor orig) {
        StompHeaderAccessor err = StompHeaderAccessor.create(StompCommand.ERROR);
        err.setLeaveMutable(true);
        err.setSessionId(orig.getSessionId()); // 이 세션으로만 전송
        err.setMessage(userMsg);
        err.setHeader("errorCode", code);
        return MessageBuilder.createMessage(new byte[0], err.getMessageHeaders());
    }

}
