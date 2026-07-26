package com.gymtracker.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "set_logs")
public class SetLog extends PanacheEntityBase {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    public String id;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column(name = "session_id", nullable = false)
    public String sessionId;

    @Column(name = "exercise_id", nullable = false)
    public String exerciseId;

    @Column(name = "exercise_name", nullable = false)
    public String exerciseName;

    @Column(name = "set_number", nullable = false)
    public Long setNumber;

    @Column(name = "set_type", nullable = false)
    public String setType;

    @Column(name = "reps_target")
    public String repsTarget;

    @Column(name = "weight_kg")
    public Double weightKg;

    @Column(name = "reps_actual")
    public Long repsActual;

    @Column(name = "completed")
    public Long completed;

    @Column(name = "rest_seconds")
    public Long restSeconds;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = Instant.now();
    }
}
