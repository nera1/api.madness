package kr.mdns.madness.projection;

public interface ChannelAndCount {
    String getPublicId();

    String getName();

    Integer getMemberCount();

    Integer getLiveCount();

    java.time.OffsetDateTime getObservedAt();
}
