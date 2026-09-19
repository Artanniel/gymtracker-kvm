package com.gymtracker.controller;

import com.gymtracker.domain.Invoice;
import com.gymtracker.domain.PaymentPlan;
import com.gymtracker.service.FinanceService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;
import java.util.Map;

@Path("/api/finance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FinanceController {

    @Inject
    FinanceService financeService;

    @Inject
    JsonWebToken jwt;

    private String getUserId() {
        if (jwt == null || jwt.getSubject() == null) {
            throw new WebApplicationException("Authentication required", Response.Status.UNAUTHORIZED);
        }
        return jwt.getSubject();
    }

    @GET
    @Path("/plans")
    public RestResponse<List<PaymentPlan>> listPlans() {
        return RestResponse.ok(financeService.getAllPlans());
    }

    @POST
    @Path("/plans")
    public RestResponse<PaymentPlan> createPlan(@Valid PaymentPlan plan) {
        PaymentPlan created = financeService.createPlan(plan);
        return RestResponse.status(Response.Status.CREATED, created);
    }

    @PUT
    @Path("/plans/{id}")
    public RestResponse<PaymentPlan> updatePlan(@PathParam("id") Long id, @Valid PaymentPlan plan) {
        PaymentPlan updated = financeService.updatePlan(id, plan);
        if (updated == null) return RestResponse.notFound();
        return RestResponse.ok(updated);
    }

    @DELETE
    @Path("/plans/{id}")
    public RestResponse<Void> deletePlan(@PathParam("id") Long id) {
        boolean deleted = financeService.deletePlan(id);
        if (!deleted) return RestResponse.notFound();
        return RestResponse.noContent();
    }

    @GET
    @Path("/invoices")
    public RestResponse<List<Invoice>> listInvoices() {
        return RestResponse.ok(financeService.getAllInvoices());
    }

    @GET
    @Path("/invoices/student/{studentId}")
    public RestResponse<List<Invoice>> getInvoicesByStudent(@PathParam("studentId") Long studentId) {
        return RestResponse.ok(financeService.getInvoicesByStudent(studentId));
    }

    @POST
    @Path("/invoices")
    public RestResponse<Invoice> createInvoice(@Valid Invoice invoice) {
        Invoice created = financeService.createInvoice(invoice);
        return RestResponse.status(Response.Status.CREATED, created);
    }

    @PUT
    @Path("/invoices/{id}/pay")
    public RestResponse<Invoice> markPaid(@PathParam("id") Long id, Map<String, String> body) {
        String method = body.getOrDefault("method", "pix");
        Invoice invoice = financeService.markPaid(id, method);
        if (invoice == null) return RestResponse.notFound();
        return RestResponse.ok(invoice);
    }

    @PUT
    @Path("/invoices/{id}/reopen")
    public RestResponse<Invoice> markPending(@PathParam("id") Long id) {
        Invoice invoice = financeService.markPending(id);
        if (invoice == null) return RestResponse.notFound();
        return RestResponse.ok(invoice);
    }

    @DELETE
    @Path("/invoices/{id}")
    public RestResponse<Void> deleteInvoice(@PathParam("id") Long id) {
        boolean deleted = financeService.deleteInvoice(id);
        if (!deleted) return RestResponse.notFound();
        return RestResponse.noContent();
    }

    @GET
    @Path("/summary")
    public RestResponse<Map<String, Double>> summary() {
        return RestResponse.ok(financeService.getSummary());
    }
}
