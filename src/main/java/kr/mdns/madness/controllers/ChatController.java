package kr.mdns.madness.controllers;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import kr.mdns.madness.dto.ChatMessageDto;
import kr.mdns.madness.services.ChatService;
import kr.mdns.madness.services.WebSocketService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final WebSocketService webSocketService;

    @MessageMapping("/chat.send.{channelId}")
    public void sendMessage(
            @DestinationVariable String channelId,
            @Payload ChatMessageDto chatMessage,
            Principal principal,
            SimpMessageHeaderAccessor headerAccessor) {

        String sender = webSocketService
                .resolvedUserDetails(principal)
                .map(userDetails -> userDetails.getMember().getNickname())
                .orElse("익명");

        chatMessage.setSender(sender);

        chatService.broadcast(channelId, chatMessage);
    }
}
