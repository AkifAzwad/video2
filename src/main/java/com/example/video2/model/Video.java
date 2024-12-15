package com.example.video2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "videos",
        indexes = {
                @Index(name = "idx_assigned_to_user_id", columnList = "assigned_to_user_id"),
                @Index(name = "idx_video_url", columnList = "videoUrl")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "assigned_to_user_id", referencedColumnName = "id")
    private User assignedToUser;
}
