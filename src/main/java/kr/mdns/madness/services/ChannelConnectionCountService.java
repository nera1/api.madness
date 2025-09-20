package kr.mdns.madness.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;

import kr.mdns.madness.record.SubscriptionKey;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelConnectionCountService {

        private final Cache<SubscriptionKey, Boolean> subscriptionCache;

        public boolean addSubscription(String publicId, Long userId, String subscriptionId) {
                SubscriptionKey key = new SubscriptionKey(publicId, userId, subscriptionId);
                boolean isFirst = subscriptionCache.asMap().keySet().stream()
                                .noneMatch(k -> k.publicId().equals(publicId) && k.userId().equals(userId));

                subscriptionCache.put(key, Boolean.TRUE);
                return isFirst;
        }

        public boolean removeSubscription(String publicId, Long userId, String subscriptionId) {
                SubscriptionKey key = new SubscriptionKey(publicId, userId, subscriptionId);
                subscriptionCache.invalidate(key);

                return subscriptionCache.asMap().keySet().stream()
                                .noneMatch(k -> k.publicId().equals(publicId) && k.userId().equals(userId));
        }

        public boolean removeSubscription(SubscriptionKey key) {
                subscriptionCache.invalidate(key);
                return subscriptionCache.asMap().remove(key) != null;
        }

        public int getSubscriptionCount(String publicId, Long userId) {
                return (int) subscriptionCache.asMap().keySet().stream()
                                .filter(k -> k.publicId().equals(publicId) && k.userId().equals(userId))
                                .count();
        }

        public int getUserCount(String publicId) {
                return (int) subscriptionCache.asMap().keySet().stream()
                                .filter(k -> k.publicId().equals(publicId))
                                .map(SubscriptionKey::userId)
                                .distinct()
                                .count();
        }

        public List<String> getTopNParticipantChannels(int topN) {
                Map<String, Set<Long>> grouped = subscriptionCache.asMap().keySet().stream()
                                .collect(Collectors.groupingBy(
                                                SubscriptionKey::publicId,
                                                Collectors.mapping(SubscriptionKey::userId, Collectors.toSet())));

                return grouped.entrySet().stream()
                                .sorted(Comparator.comparingInt((Map.Entry<String, Set<Long>> e) -> e.getValue().size())
                                                .reversed())
                                .limit(topN)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());
        }

        public Set<Long> getUserIds(String publicId) {
                return subscriptionCache.asMap().keySet().stream()
                                .filter(k -> k.publicId().equals(publicId))
                                .map(SubscriptionKey::userId)
                                .collect(Collectors.toSet());
        }

        public Map<String, Integer> snapshotCounts() {
                Map<String, Set<Long>> grouped = subscriptionCache.asMap().keySet().stream()
                                .collect(Collectors.groupingBy(
                                                SubscriptionKey::publicId,
                                                Collectors.mapping(SubscriptionKey::userId, Collectors.toSet())));
                return grouped.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
        }
}
