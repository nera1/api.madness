package kr.mdns.madness.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "channels", indexes = {
        @Index(name = "idx_channel_public_id", columnList = "public_id"),
        @Index(name = "idx_channel_member_count", columnList = "member_count"),
        @Index(name = "idx_channel_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false, length = 36)
    private String publicId;

    @Column(nullable = false)
    private String name;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "member_count", nullable = false)
    private int memberCount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
