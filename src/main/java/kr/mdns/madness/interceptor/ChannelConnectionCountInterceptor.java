package kr.mdns.madness.interceptor;

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

        String subscriptionIdFull = accessor.getSubscriptionId();
        if (subscriptionIdFull == null) {
            return;
        }

        String publicId = webSocketService
                .extractPublicChannelIdFromSubscriptionId(subscriptionIdFull);
        String randomId = webSocketService
                .extractRandomIdFromSubscriptionId(subscriptionIdFull);

        Long userId = ((CustomUserDetails) ((Authentication) accessor.getUser())
                .getPrincipal()).getId();

        if (StompCommand.SUBSCRIBE.equals(cmd)) {
            channelConnectionCountService.addSubscription(
                    publicId, userId, randomId);
            channelConnectionCountService.getUserCount(publicId);
            return;
        }
        if (StompCommand.UNSUBSCRIBE.equals(cmd)) {
            int prev = channelConnectionCountService.getUserCount(publicId);
            System.out.println(prev);
            channelConnectionCountService.removeSubscription(
                    publicId, userId, randomId);
            int next = channelConnectionCountService.getUserCount(publicId);
            System.out.println(next);
            return;
        }

    }
}
