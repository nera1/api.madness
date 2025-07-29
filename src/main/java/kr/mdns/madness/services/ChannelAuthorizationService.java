package kr.mdns.madness.services;

import java.security.Principal;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelAuthorizationService {
    private final ChannelMemberService channelMemberService;
    private final WebSocketService webSocketService;

    public void checkWebsocketChannelAccess(Principal principal, String destination) {
        Long userId = webSocketService
                .resolvedUserId(principal)
                .orElseThrow(() -> new IllegalStateException("WebSocket 인증 정보 없음"));

        String publicChannelId = webSocketService.extractPublicChannelIdFromDestination(destination);

        if (!channelMemberService.findJoinedChannelIds(userId).contains(publicChannelId)) {
            throw new AccessDeniedException("채널 접근 권한이 없습니다. channel=" + publicChannelId);
        }
    }
}
