package kr.mdns.madness.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import kr.mdns.madness.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcast(String channelId, ChatMessageDto message) {
        String destination = String.format("/sub/chat.%s", channelId);
        messagingTemplate.convertAndSend(destination, message);
    }
}
