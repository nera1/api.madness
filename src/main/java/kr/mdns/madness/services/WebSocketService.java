package kr.mdns.madness.services;

import java.security.Principal;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import kr.mdns.madness.security.CustomUserDetails;

@Component
public class WebSocketService {
    public Optional<Long> resolvedUserId(Principal principal) {
        if (!(principal instanceof Authentication)) {
            return Optional.empty();
        }
        Object principalObj = ((Authentication) principal).getPrincipal();
        if (principalObj instanceof CustomUserDetails) {
            return Optional.of(((CustomUserDetails) principalObj).getId());
        }
        return Optional.empty();
    }

    public String extractPublicChannelIdFromDestination(String destination) {
        int idx = destination.lastIndexOf('.');
        if (idx < 0 || idx == destination.length() - 1) {
            throw new IllegalArgumentException("잘못된 destination: " + destination);
        }
        return destination.substring(idx + 1);
    }
}
