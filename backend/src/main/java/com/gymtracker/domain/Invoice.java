package com.gymtracker.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "invoices")
public class Invoice extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "student_id", nullable = false)
    public Long studentId;

    @Column(name = "plan_id")
    public Long planId;

    @Column(name = "amount", nullable = false)
    public Double amount;

    @Column(name = "currency", nullable = false)
    public String currency = "BRL";

    @Column(name = "due_date", nullable = false)
    public Long dueDate;

    @Column(name = "paid_at")
    public Long paidAt;

    @Column(name = "status", nullable = false)
    public String status = "pending";

    @Column(name = "description")
    public String description;

    @Column(name = "payment_method")
    public String paymentMethod;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
