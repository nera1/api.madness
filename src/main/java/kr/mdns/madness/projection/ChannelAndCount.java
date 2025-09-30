package kr.mdns.madness.projection;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface ChannelAndCount {
    String getPublicId();

    String getName();

    Integer getMemberCount();

    Integer getLiveCount();

    OffsetDateTime getSnapAt();

    LocalDateTime getCreatedAt();
}
