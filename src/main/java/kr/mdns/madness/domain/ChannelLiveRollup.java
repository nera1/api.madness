package kr.mdns.madness.domain;

import java.time.OffsetDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "channel_live_rollup", indexes = {
        @Index(name = "idx_rollup_public_id", columnList = "public_id"),
        @Index(name = "idx_rollup_snap_at", columnList = "snap_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_channel_live_rollup_pubid_snapat", columnNames = { "public_id", "snap_at" })
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChannelLiveRollup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "live_count", nullable = false)
    private Integer liveCount;

    @Column(name = "snap_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime snapAt;
}
