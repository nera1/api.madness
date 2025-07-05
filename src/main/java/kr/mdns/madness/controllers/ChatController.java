package kr.mdns.madness.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import kr.mdns.madness.dto.ChatMessageDto;
import kr.mdns.madness.services.ChatService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat.send.{channelId}")
    public void sendMessage(
            @DestinationVariable String channelId,
            ChatMessageDto chatMessage) {
        chatService.broadcast(channelId, chatMessage);
    }
}
