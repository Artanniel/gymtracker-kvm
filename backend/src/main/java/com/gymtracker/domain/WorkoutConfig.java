package com.gymtracker.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workout_configs")
public class WorkoutConfig extends PanacheEntityBase {

    @Id
    @Column(name = "workout_id", updatable = false, nullable = false)
    public String workoutId;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column(name = "is_active")
    public Long isActive;

    @Column(name = "start_date")
    public Long startDate;

    @Column(name = "end_date")
    public Long endDate;

    @Column(name = "is_archived")
    public Long isArchived;

    @Column(name = "cover_image")
    public String coverImage;

    @Column(name = "youtube_url")
    public String youtubeUrl;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (isActive == null) isActive = 0L;
        if (isArchived == null) isArchived = 0L;
    }
}
