package kr.mdns.madness.projection;

import java.time.Instant;

public interface ChannelAndCount {
    String getPublicId();

    String getName();

    Integer getMemberCount();

    Integer getLiveCount();

    Instant getSnapAt();

    Instant getCreatedAt();
}
