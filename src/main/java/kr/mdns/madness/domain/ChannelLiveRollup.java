package kr.mdns.madness.domain;

import java.time.OffsetDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "channel_live_rollup")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChannelLiveRollup {

    /** 채널 식별자 (channels.public_id와 1:1) */
    @Id
    @Column(name = "public_id", nullable = false, updatable = false)
    private String publicId;

    /** 현재 집계 인원수 */
    @Column(name = "live_count", nullable = false)
    private Integer liveCount;

    /** 관측 시각(선택). DB에서 now()로 채우거나 UPSERT 시 갱신 */
    @Column(name = "observed_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime observedAt;
}
