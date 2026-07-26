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
            "quarkus.oidc.enabled", "false"
        );
    }
}
