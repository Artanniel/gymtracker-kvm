package com.gymtracker.service;

import com.gymtracker.domain.Invoice;
import com.gymtracker.domain.PaymentPlan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FinanceService {

    public List<PaymentPlan> getAllPlans() {
        return PaymentPlan.findAll().list();
    }

    @Transactional
    public PaymentPlan createPlan(PaymentPlan plan) {
        plan.persist();
        return plan;
    }

    @Transactional
    public PaymentPlan updatePlan(Long id, PaymentPlan updates) {
        PaymentPlan plan = PaymentPlan.findById(id);
        if (plan != null) {
            plan.name = updates.name;
            plan.description = updates.description;
            plan.durationMonths = updates.durationMonths;
            plan.price = updates.price;
            plan.recurrence = updates.recurrence;
            plan.isActive = updates.isActive;
            plan.persist();
        }
        return plan;
    }

    @Transactional
    public boolean deletePlan(Long id) {
        PaymentPlan plan = PaymentPlan.findById(id);
        if (plan != null) {
            plan.delete();
            return true;
        }
        return false;
    }

    public List<Invoice> getAllInvoices() {
        return Invoice.findAll().list();
    }

    public List<Invoice> getInvoicesByStudent(Long studentId) {
        return Invoice.find("studentId = ?1 ORDER BY dueDate DESC", studentId).list();
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        invoice.persist();
        return invoice;
    }

    @Transactional
    public Invoice markPaid(Long id, String method) {
        Invoice invoice = Invoice.findById(id);
        if (invoice != null) {
            invoice.status = "paid";
            invoice.paidAt = Instant.now().toEpochMilli();
            invoice.paymentMethod = method;
            invoice.persist();
        }
        return invoice;
    }

    @Transactional
    public Invoice markPending(Long id) {
        Invoice invoice = Invoice.findById(id);
        if (invoice != null) {
            invoice.status = "pending";
            invoice.paidAt = null;
            invoice.paymentMethod = null;
            invoice.persist();
        }
        return invoice;
    }

    @Transactional
    public boolean deleteInvoice(Long id) {
        Invoice invoice = Invoice.findById(id);
        if (invoice != null) {
            invoice.delete();
            return true;
        }
        return false;
    }

    public Map<String, Double> getSummary() {
        List<Invoice> invoices = Invoice.findAll().list();
        double totalPaid = 0;
        double totalPending = 0;
        double totalOverdue = 0;
        long now = Instant.now().toEpochMilli();

        for (Invoice invoice : invoices) {
            switch (invoice.status) {
                case "paid":
                    totalPaid += invoice.amount;
                    break;
                case "pending":
                    if (invoice.dueDate < now) {
                        totalOverdue += invoice.amount;
                    } else {
                        totalPending += invoice.amount;
                    }
                    break;
            }
        }

        Map<String, Double> summary = new HashMap<>();
        summary.put("totalPaid", totalPaid);
        summary.put("totalPending", totalPending);
        summary.put("totalOverdue", totalOverdue);
        return summary;
    }
}
