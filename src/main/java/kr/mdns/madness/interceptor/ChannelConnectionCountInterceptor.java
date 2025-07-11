package kr.mdns.madness.interceptor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import kr.mdns.madness.services.ChannelConnectionCountService;
import kr.mdns.madness.services.WebSocketService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConnectionCountInterceptor implements ChannelInterceptor {
    private final ChannelConnectionCountService channelConnectionCountService;
    private final WebSocketService webSocketService;

    private final ConcurrentMap<String, Set<String>> channelSessions = new ConcurrentHashMap<>();

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand cmd = accessor.getCommand();
        String sessionId = accessor.getSessionId();

        if (StompCommand.DISCONNECT.equals(cmd)) {
            channelSessions.forEach((publicId, sessions) -> {
                if (sessions.remove(sessionId)) {
                    channelConnectionCountService.decrement(publicId);
                    System.out.println(publicId + " - 1 (forced by DISCONNECT)");
                }
            });
            return;
        }

        String dest = accessor.getDestination();
        if (dest == null || !dest.contains(".")) {
            return;
        }
        String publicId = webSocketService.extractPublicChannelIdFromDestination(dest);

        if (StompCommand.SUBSCRIBE.equals(cmd)) {
            Set<String> sessions = channelSessions
                    .computeIfAbsent(publicId, id -> ConcurrentHashMap.newKeySet());
            if (sessions.add(sessionId)) {
                channelConnectionCountService.increment(publicId);
                System.out.println(publicId + " + 1");
            }
        } else if (StompCommand.UNSUBSCRIBE.equals(cmd)) {
            Set<String> sessions = channelSessions.get(publicId);
            if (sessions != null && sessions.remove(sessionId)) {
                channelConnectionCountService.decrement(publicId);
                System.out.println(publicId + " - 1");
            }
        }
    }
}
