package kr.mdns.madness.scheduler;

import java.time.Duration;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.mdns.madness.services.ChannelLiveRollupSyncService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConnectionCountScheduler {
    private final ChannelLiveRollupSyncService channelLiveRollupSyncService;

    @Scheduled(cron = "10 */5 * * * *", zone = "Asia/Seoul")
    public void sumChannelConnectionCountByCache() {
        channelLiveRollupSyncService.replaceAllSnapshot();
    }

    @Scheduled(cron = "20 2/30 * * * *", zone = "Asia/Seoul")
    public void purgeOldSnapshots() {
        channelLiveRollupSyncService.deleteStaleOlderThanLatestMinus(Duration.ofMinutes(30));
    }
}
