package kr.mdns.madness.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import kr.mdns.madness.services.ChannelMemberService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final ChannelMemberService channelMemberService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // TODO Auto-generated method stub
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
