package kr.mdns.madness.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelConnectionCountService {
    private static final String CACHE_NAME = "channelConnectedCount";

    private final CacheManager cacheManager;

    private Cache cache() {
        Cache c = cacheManager.getCache(CACHE_NAME);
        if (c == null) {
            throw new IllegalStateException("Cache '" + CACHE_NAME + "' is not configured");
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Set<String>> getUserMap(String publicId) {
        Cache.ValueWrapper wrapper = cache().get(publicId);
        return (wrapper == null)
                ? null
                : (Map<Long, Set<String>>) wrapper.get();
    }

    public boolean addSubscription(String publicId, Long userId, String subscriptionId) {
        Map<Long, Set<String>> userMap = getUserMap(publicId);
        if (userMap == null) {
            userMap = new ConcurrentHashMap<>();
        }

        Set<String> subs = userMap.computeIfAbsent(userId, id -> ConcurrentHashMap.newKeySet());
        boolean firstTab = subs.isEmpty();
        subs.add(subscriptionId);

        cache().put(publicId, userMap);
        return firstTab;
    }

    public boolean removeSubscription(String publicId, Long userId, String subscriptionId) {
        Map<Long, Set<String>> userMap = getUserMap(publicId);
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
        }

        if (userMap.isEmpty()) {
            cache().evict(publicId);
        } else {
            cache().put(publicId, userMap);
        }

        return lastTab;
    }

    public int getSubscriptionCount(String publicId, Long userId) {
        Map<Long, Set<String>> userMap = getUserMap(publicId);
        if (userMap == null || !userMap.containsKey(userId)) {
            return 0;
        }
        return userMap.get(userId).size();
    }

    public int getUserCount(String publicId) {
        Map<Long, Set<String>> userMap = getUserMap(publicId);
        return (userMap == null) ? 0 : userMap.size();
    }

    public List<String> getTopNParticipantChannels(int topN) {
        Cache springCache = cache();
        if (!(springCache instanceof CaffeineCache)) {
            throw new IllegalStateException(
                    "Cache '" + CACHE_NAME + "' is not a CaffeineCache");
        }
        CaffeineCache caffeineCache = (CaffeineCache) springCache;

        var nativeMap = caffeineCache.getNativeCache().asMap();

        return nativeMap.keySet().stream()
                .map(Object::toString)
                .sorted(Comparator
                        .comparingInt(this::getUserCount)
                        .reversed()
                        .thenComparing(Comparator.reverseOrder()))
                .limit(topN)
                .collect(Collectors.toList());
    }

    public Set<Long> getUserIds(String publicId) {
        Map<Long, Set<String>> userMap = getUserMap(publicId);
        return (userMap == null)
                ? Set.of()
                : userMap.keySet();
    }
}
