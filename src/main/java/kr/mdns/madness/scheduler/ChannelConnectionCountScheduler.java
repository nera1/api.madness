package kr.mdns.madness.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.mdns.madness.services.ChannelLiveRollupSyncService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConnectionCountScheduler {
    private final ChannelLiveRollupSyncService channelLiveRollupSyncService;

    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul")
    public void sumChannelConnectionCountByCache() {
        channelLiveRollupSyncService.replaceAllSnapshot();
    }
}
