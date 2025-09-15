package kr.mdns.madness.interceptor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import kr.mdns.madness.record.SubscriptionKey;
import kr.mdns.madness.security.CustomUserDetails;
import kr.mdns.madness.services.ChannelConnectionCountService;
import kr.mdns.madness.services.WebSocketService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConnectionCountInterceptor implements ChannelInterceptor {

    private static final String ATTR_SUBS = "WS_SUBS";

    private final ChannelConnectionCountService channelConnectionCountService;
    private final WebSocketService webSocketService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand cmd = accessor.getCommand();

        if (cmd == StompCommand.SUBSCRIBE || cmd == StompCommand.UNSUBSCRIBE) {

            Map<String, Object> attrs = accessor.getSessionAttributes();

            if (attrs == null) {
                attrs = new ConcurrentHashMap<>();
                accessor.setSessionAttributes(attrs);
            }

            String subscriptionIdFull = accessor.getSubscriptionId();

            String publicId = webSocketService
                    .extractPublicChannelIdFromSubscriptionId(subscriptionIdFull);
            String randomId = webSocketService
                    .extractRandomIdFromSubscriptionId(subscriptionIdFull);

            Long userId = ((CustomUserDetails) ((Authentication) accessor.getUser())
                    .getPrincipal()).getId();

            Set<SubscriptionKey> subs = (Set<SubscriptionKey>) attrs.computeIfAbsent(ATTR_SUBS,
                    key -> ConcurrentHashMap.newKeySet());

            SubscriptionKey subKey = new SubscriptionKey(publicId, userId, randomId);

            if (StompCommand.SUBSCRIBE.equals(cmd)) {

                subs.add(subKey);

                channelConnectionCountService.addSubscription(
                        publicId, userId, randomId);

            }

            if (StompCommand.UNSUBSCRIBE.equals(cmd)) {
                subs.remove(subKey);

                channelConnectionCountService.removeSubscription(
                        publicId, userId, randomId);
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
