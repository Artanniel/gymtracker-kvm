package com.gymtracker.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    private final Client httpClient = ClientBuilder.newClient();

    @POST
    @Path("/login")
    public Response login(Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Email and password required"))
                    .build();
        }

        try {
            Form form = new Form()
                    .param("grant_type", "password")
                    .param("client_id", clientId)
                    .param("client_secret", clientSecret)
                    .param("username", email)
                    .param("password", password)
                    .param("scope", "openid profile email");

            String tokenUrl = authServerUrl + "/protocol/openid-connect/token";

            Response kcResponse = httpClient.target(tokenUrl)
                    .request()
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            int status = kcResponse.getStatus();
            String entity = kcResponse.readEntity(String.class);

            return Response.status(status)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(entity)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(Map.of("error", "Auth server unavailable", "message", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/register")
    public Response register(Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");

        if (name == null || email == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Name, email and password required"))
                    .build();
        }

        try {
            String adminToken = getAdminToken();

            String[] nameParts = name.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            Map<String, Object> userPayload = Map.of(
                    "username", email,
                    "email", email,
                    "firstName", firstName,
                    "lastName", lastName,
                    "enabled", true,
                    "emailVerified", false,
                    "credentials", new Object[]{
                            Map.of("type", "password", "value", password, "temporary", false)
                    }
            );

            String usersUrl = authServerUrl.replace("/realms/", "/admin/realms/") + "/users";

            Response kcResponse = httpClient.target(usersUrl)
                    .request()
                    .header("Authorization", "Bearer " + adminToken)
                    .post(Entity.json(userPayload));

            if (kcResponse.getStatus() == 201) {
                return Response.status(Response.Status.CREATED)
                        .entity(Map.of("message", "User registered successfully"))
                        .build();
            } else {
                String error = kcResponse.readEntity(String.class);
                return Response.status(kcResponse.getStatus())
                        .entity(Map.of("error", "Registration failed", "details", error))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(Map.of("error", "Auth server unavailable", "message", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/refresh")
    public Response refresh(Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Refresh token required"))
                    .build();
        }

        try {
            Form form = new Form()
                    .param("grant_type", "refresh_token")
                    .param("client_id", clientId)
                    .param("client_secret", clientSecret)
                    .param("refresh_token", refreshToken);

            String tokenUrl = authServerUrl + "/protocol/openid-connect/token";

            Response kcResponse = httpClient.target(tokenUrl)
                    .request()
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

            int status = kcResponse.getStatus();
            String entity = kcResponse.readEntity(String.class);

            return Response.status(status)
                    .header("Access-Control-Allow-Origin", "*")
                    .entity(entity)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(Map.of("error", "Auth server unavailable", "message", e.getMessage()))
                    .build();
        }
    }

    @OPTIONS
    @Path("/{path:.*}")
    public Response corsPreflight() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Authorization")
                .build();
    }

    private String getAdminToken() {
        Form form = new Form()
                .param("grant_type", "client_credentials")
                .param("client_id", clientId)
                .param("client_secret", clientSecret);

        String tokenUrl = authServerUrl + "/protocol/openid-connect/token";

        Response response = httpClient.target(tokenUrl)
                .request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        Map<String, Object> tokenResponse = response.readEntity(Map.class);
        return (String) tokenResponse.get("access_token");
    }
}
