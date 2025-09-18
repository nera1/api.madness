package kr.mdns.madness.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.mdns.madness.services.ChannelConnectionCountService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConnectionCountScheduler {
    private final ChannelConnectionCountService channelConnectionCountService;

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void sumChannelConnectionCountByCache() {

    }
}
