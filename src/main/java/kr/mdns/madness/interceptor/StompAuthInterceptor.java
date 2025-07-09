package kr.mdns.madness.interceptor;

import java.security.Principal;
import java.util.Set;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import kr.mdns.madness.security.CustomUserDetails;
import kr.mdns.madness.services.ChannelMemberService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final ChannelMemberService channelMemberService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Principal p = accessor.getUser();
        if (p instanceof Authentication) {
            Authentication auth = (Authentication) p;
            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
