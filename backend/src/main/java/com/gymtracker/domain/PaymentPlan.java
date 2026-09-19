package com.gymtracker.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payment_plans")
public class PaymentPlan extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;

    @Column(name = "duration_months", nullable = false)
    public Integer durationMonths = 1;

    @Column(name = "price", nullable = false)
    public Double price;

    @Column(name = "currency", nullable = false)
    public String currency = "BRL";

    @Column(name = "recurrence", nullable = false)
    public String recurrence = "monthly";

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (isActive == null) isActive = true;
    }
}
