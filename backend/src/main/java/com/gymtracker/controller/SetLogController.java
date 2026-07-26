package com.gymtracker.controller;

import com.gymtracker.domain.SetLog;
import com.gymtracker.service.WorkoutService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/workout-sessions/{sessionId}/sets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SetLogController {

    @Inject
    WorkoutService workoutService;

    @Inject
    JsonWebToken jwt;

    private String getUserId() {
        return jwt.getSubject();
    }

    @GET
    public RestResponse<List<SetLog>> listBySession(@PathParam("sessionId") String sessionId) {
        return RestResponse.ok(workoutService.getSetLogsBySession(getUserId(), sessionId));
    }

    @POST
    public RestResponse<SetLog> create(
            @PathParam("sessionId") String sessionId,
            @Valid SetLog setLog) {
        setLog.sessionId = sessionId;
        SetLog created = workoutService.createSetLog(getUserId(), setLog);
        return RestResponse.status(Response.Status.CREATED, created);
    }

    @PUT
    @Path("/{exerciseId}/{setNumber}")
    public RestResponse<SetLog> update(
            @PathParam("sessionId") String sessionId,
            @PathParam("exerciseId") String exerciseId,
            @PathParam("setNumber") Long setNumber,
            @Valid SetLog setLog) {
        SetLog updated = workoutService.updateSetLog(getUserId(), sessionId, exerciseId, setNumber, setLog);
        if (updated == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(updated);
    }

    @DELETE
    public RestResponse<Void> deleteBySession(@PathParam("sessionId") String sessionId) {
        boolean deleted = workoutService.deleteSetLogsBySession(getUserId(), sessionId);
        if (!deleted) {
            return RestResponse.notFound();
        }
        return RestResponse.noContent();
    }
}
