package kr.mdns.madness.interceptor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import kr.mdns.madness.security.CustomUserDetails;
import kr.mdns.madness.services.ChannelConnectionCountService;
import kr.mdns.madness.services.WebSocketService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConnectionCountInterceptor implements ChannelInterceptor {
    private final ChannelConnectionCountService channelConnectionCountService;
    private final WebSocketService webSocketService;

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand cmd = accessor.getCommand();

        Long userId = ((CustomUserDetails) ((Authentication) accessor.getUser()).getPrincipal()).getId();

        String dest = accessor.getDestination();

        if (dest == null || !dest.contains(".")) {
            return;
        }

        String publicId = webSocketService.extractPublicChannelIdFromDestination(dest);

        if (StompCommand.DISCONNECT.equals(cmd)) {
            int count = channelConnectionCountService.removeUser(publicId, userId);
            System.out.println(publicId + " 현재 접속 유저 수: " + count);
            return;
        }

        if (StompCommand.SUBSCRIBE.equals(cmd)) {
            int count = channelConnectionCountService.addUser(publicId, userId);
            System.out.println(publicId + " 현재 접속 유저 수: " + count);
            return;
        }

        if (StompCommand.UNSUBSCRIBE.equals(cmd) || StompCommand.DISCONNECT.equals(cmd)) {
            int count = channelConnectionCountService.removeUser(publicId, userId);
            System.out.println(publicId + " 현재 접속 유저 수: " + count);
            return;
        }
    }
}
