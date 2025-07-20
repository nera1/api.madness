package kr.mdns.madness.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelConnectionCountService {
    private static final String CACHE_NAME = "channelConnectedCount";

    private final CacheManager cacheManager;
    private final ConcurrentHashMap<String, AtomicInteger> userCountMap = new ConcurrentHashMap<>();

    private Cache cache() {
        Cache c = cacheManager.getCache(CACHE_NAME);
        if (c == null) {
            throw new IllegalStateException("Cache '" + CACHE_NAME + "' is not configured");
        }
        return c;
    }

    public boolean addSubscription(String publicId, Long userId, String subscriptionId) {
        Map<Long, Set<String>> userMap = cache().get(publicId, Map.class);

        if (userMap == null) {
            userMap = new ConcurrentHashMap<>();
        }

        Set<String> subs = userMap.computeIfAbsent(
                userId,
                id -> ConcurrentHashMap.newKeySet());

        boolean firstTab = subs.isEmpty();
        subs.add(subscriptionId);

        cache().put(publicId, userMap);

        if (firstTab) {
            userCountMap
                    .computeIfAbsent(publicId, key -> new AtomicInteger(0))
                    .incrementAndGet();
        }

        return firstTab;
    }

    public boolean removeSubscription(String publicId, Long userId, String subscriptionId) {
        Map<Long, Set<String>> userMap = cache().get(publicId, Map.class);

        if (userMap == null) {
            return false;
        }

        Set<String> subs = userMap.get(userId);
        if (subs == null) {
            return false;
        }

        subs.remove(subscriptionId);
        boolean lastTab = subs.isEmpty();

        if (lastTab) {
            userMap.remove(userId);
            AtomicInteger cnt = userCountMap.get(publicId);
            if (cnt != null) {
                if (cnt.decrementAndGet() == 0) {
                    userCountMap.remove(publicId);
                }
            }
        }

        if (userMap.isEmpty()) {
            cache().evict(publicId);
        } else {
            cache().put(publicId, userMap);
        }

        return lastTab;
    }

    public int getSubscriptionCount(String publicId, Long userId) {
        Map<Long, Set<String>> userMap = cache().get(publicId, Map.class);
        if (userMap == null || !userMap.containsKey(userId)) {
            return 0;
        }
        return userMap.get(userId).size();
    }

    public int getUserCount(String publicId) {
        AtomicInteger cnt = userCountMap.get(publicId);
        return cnt == null ? 0 : cnt.get();
    }

    public List<String> getTopChannels(int topN) {
        return userCountMap.entrySet().stream()
                .sorted(Comparator
                        .comparingInt((Map.Entry<String, AtomicInteger> e) -> e.getValue().get())
                        .reversed()
                        .thenComparing(Map.Entry.<String, AtomicInteger>comparingByKey(Comparator.reverseOrder())))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Set<Long> getUserIds(String publicId) {
        Map<Long, Set<String>> userMap = cache().get(publicId, Map.class);
        return userMap == null
                ? Set.of()
                : userMap.keySet();
    }
}
