package kr.mdns.madness.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConnectionCountScheduler {
    private final ChannelConnectionCountScheduler channelConnectionCountScheduler;

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void sumChannelConnectionCountByCache() {

    }
}
