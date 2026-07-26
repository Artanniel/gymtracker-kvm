package com.gymtracker.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workout_sessions")
public class WorkoutSession extends PanacheEntityBase {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    public String id;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column(name = "workout_id", nullable = false)
    public String workoutId;

    @Column(name = "workout_name", nullable = false)
    public String workoutName;

    @Column(name = "date", nullable = false)
    public Long date;

    @Column(name = "notes")
    public String notes;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at")
    public Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
