package com.gymtracker.controller;

import com.gymtracker.domain.WorkoutSession;
import com.gymtracker.service.WorkoutService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/workout-sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkoutSessionController {

    @Inject
    WorkoutService workoutService;

    @Inject
    JsonWebToken jwt;

    private String getUserId() {
        if (jwt == null || jwt.getSubject() == null) {
            throw new WebApplicationException("Authentication required", Response.Status.UNAUTHORIZED);
        }
        return jwt.getSubject();
    }

    @GET
    public RestResponse<List<WorkoutSession>> listAll() {
        return RestResponse.ok(workoutService.getSessions(getUserId()));
    }

    @GET
    @Path("/{id}")
    public RestResponse<WorkoutSession> getById(@PathParam("id") String id) {
        WorkoutSession session = workoutService.getSessionById(getUserId(), id);
        if (session == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(session);
    }

    @POST
    public RestResponse<WorkoutSession> create(@Valid WorkoutSession session) {
        WorkoutSession created = workoutService.createSession(getUserId(), session);
        return RestResponse.status(Response.Status.CREATED, created);
    }

    @PUT
    @Path("/{id}")
    public RestResponse<WorkoutSession> update(@PathParam("id") String id, @Valid WorkoutSession session) {
        WorkoutSession updated = workoutService.updateSession(getUserId(), id, session);
        if (updated == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(updated);
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<Void> delete(@PathParam("id") String id) {
        boolean deleted = workoutService.deleteSession(getUserId(), id);
        if (!deleted) {
            return RestResponse.notFound();
        }
        return RestResponse.noContent();
    }
}
