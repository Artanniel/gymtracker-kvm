package com.gymtracker.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "exercise_videos")
public class ExerciseVideo extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id")
    public String userId;

    @Column(name = "exercise_id", nullable = false)
    public String exerciseId;

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "url", nullable = false)
    public String url;

    @Column(name = "thumbnail_url")
    public String thumbnailUrl;

    @Column(name = "duration")
    public Long duration;

    @Column(name = "description")
    public String description;

    @Column(name = "muscle_group", nullable = false)
    public String muscleGroup;

    @Column(name = "category", nullable = false)
    public String category;

    @Column(name = "is_custom", nullable = false)
    public Boolean isCustom = false;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (isCustom == null) isCustom = false;
    }
}
