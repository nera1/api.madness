package kr.mdns.madness.services;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelConnectionCountService {

    private final ConcurrentMap<String, Set<Long>> userMap = new ConcurrentHashMap<>();

    public int addUser(String publicId, Long userId) {
        Set<Long> users = userMap.computeIfAbsent(publicId, id -> ConcurrentHashMap.newKeySet());
        users.add(userId);
        return users.size();
    }

    public int removeUser(String publicId, Long userId) {
        Set<Long> users = userMap.get(publicId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                userMap.remove(publicId);
            }
            return users.size();
        }
        return 0;
    }

    public int getCount(String publicId) {
        return userMap.getOrDefault(publicId, Collections.emptySet()).size();
    }

    public Set<Long> getUserIds(String publicId) {
        return Collections.unmodifiableSet(userMap.getOrDefault(publicId, Collections.emptySet()));
    }
}
