package kr.mdns.madness.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import kr.mdns.madness.services.ChannelAuthorizationService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final ChannelAuthorizationService channelAuthrizationService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        StompCommand cmd = accessor.getCommand();

        if (cmd == StompCommand.SUBSCRIBE
                || cmd == StompCommand.SEND) {
            channelAuthrizationService.checkWebsocketChannelAccess(accessor.getUser(), accessor.getDestination());
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }
}
