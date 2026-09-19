package com.gymtracker.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

@Path("/api/push")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PushController {

    @Inject
    JsonWebToken jwt;

    private String getUserId() {
        if (jwt == null || jwt.getSubject() == null) {
            throw new WebApplicationException("Authentication required", Response.Status.UNAUTHORIZED);
        }
        return jwt.getSubject();
    }

    @POST
    @Path("/token")
    public RestResponse<Map<String, String>> registerToken(Map<String, String> body) {
        String token = body.get("token");
        String platform = body.get("platform");
        if (token == null || platform == null) {
            return RestResponse.status(Response.Status.BAD_REQUEST, Map.of("error", "token and platform required"));
        }
        // In production: store token associated with userId
        return RestResponse.ok(Map.of("message", "Token registered", "userId", getUserId()));
    }

    @DELETE
    @Path("/token")
    public RestResponse<Map<String, String>> unregisterToken() {
        return RestResponse.ok(Map.of("message", "Token unregistered", "userId", getUserId()));
    }
}
