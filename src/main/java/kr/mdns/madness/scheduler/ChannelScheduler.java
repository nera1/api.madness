package kr.mdns.madness.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.mdns.madness.services.ChannelService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelScheduler {
    private final ChannelService channelService;

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void refreshTopMemberJoinedChannelsCache() {
        channelService.refreshTopMemberJoinedChannels(10);
    }
}
