package com.gymtracker.controller;

import com.gymtracker.dto.SyncRequest;
import com.gymtracker.dto.SyncResponse;
import com.gymtracker.service.SyncService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/sync")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SyncController {

    @Inject
    SyncService syncService;

    @Inject
    JsonWebToken jwt;

    private String getUserId() {
        return jwt.getSubject();
    }

    @POST
    public RestResponse<List<SyncResponse>> sync(@Valid List<SyncRequest> requests) {
        List<SyncResponse> responses = syncService.processSyncBatch(getUserId(), requests);
        return RestResponse.ok(responses);
    }

    @GET
    @Path("/pull")
    public RestResponse<List<Object>> pull(
            @QueryParam("entityType") String entityType,
            @QueryParam("since") Long since) {
        List<Object> data = syncService.pullData(getUserId(), entityType, since);
        return RestResponse.ok(data);
    }

    @GET
    @Path("/health")
    public RestResponse<HealthStatus> health() {
        HealthStatus status = new HealthStatus();
        status.status = "UP";
        status.service = "gymtracker-backend";
        return RestResponse.ok(status);
    }

    public static class HealthStatus {
        public String status;
        public String service;
    }
}
