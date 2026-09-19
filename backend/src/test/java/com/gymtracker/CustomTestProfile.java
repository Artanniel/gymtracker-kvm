package com.gymtracker;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

/**
 * Custom test profile to avoid port conflicts with Keycloak (8081)
 */
public class CustomTestProfile implements QuarkusTestProfile {
    
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.http.port", "8090",
            "quarkus.keycloak.devservices.enabled", "false",
            "quarkus.oidc.enabled", "false",
            // AuthController injects these via plain @ConfigProperty, so they're
            // required even with OIDC disabled — getConfigOverrides() replaces
            // the %test.* properties from application.properties for tests
            // using this profile, so they must be repeated here too.
            "quarkus.oidc.auth-server-url", "http://localhost:8181/realms/gymtracker",
            "quarkus.oidc.client-id", "gymtracker-backend",
            "quarkus.oidc.credentials.secret", "secret"
        );
    }
}
