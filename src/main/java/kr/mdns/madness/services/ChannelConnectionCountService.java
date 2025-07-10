package kr.mdns.madness.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelConnectionCountService {

    private final ConcurrentHashMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    public int increment(String publicId) {
        return counterMap
                .computeIfAbsent(publicId, id -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public int decrement(String publicId) {
        return counterMap
                .getOrDefault(publicId, new AtomicInteger(0))
                .decrementAndGet();
    }

    public int getCount(String publicId) {
        return counterMap.getOrDefault(publicId, new AtomicInteger(0)).get();
    }
}
