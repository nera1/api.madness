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

        // System.out.println(accessor.getCommand());

        if (accessor.getCommand() == StompCommand.SUBSCRIBE
                || accessor.getCommand() == StompCommand.SEND) {

            Principal principal = accessor.getUser();
            if (!(principal instanceof Authentication)) {
                return message;
            }

            Authentication auth = (Authentication) principal;
            Object userObj = auth.getPrincipal();
            if (!(userObj instanceof CustomUserDetails)) {
                return message;
            }

            Long userId = ((CustomUserDetails) userObj).getId();

            String dest = accessor.getDestination();
            String publicChannelId = dest.substring(dest.lastIndexOf('.') + 1);

            Set<String> joined = channelMemberService.findJoinedChannelIds(userId);

            if (!joined.contains(publicChannelId)) {
                throw new IllegalArgumentException("해당 채널에 대한 접근 권한이 없습니다.");
            }
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }
}
