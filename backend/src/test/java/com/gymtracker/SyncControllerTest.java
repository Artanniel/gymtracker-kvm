package com.gymtracker;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestProfile(CustomTestProfile.class)
public class SyncControllerTest {

    @Test
    public void testSyncEndpoint() {
        // With OIDC disabled in test, JWT is null → 401 Unauthorized
        String syncJson = """
            [
                {
                    "entityType": "workout_session",
                    "entityId": "test-123",
                    "action": "CREATE",
                    "payload": "{}"
                }
            ]
            """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(syncJson)
            .when().post("/api/sync")
            .then()
            .statusCode(401);
    }

    @Test
    public void testPullEndpoint() {
        // With OIDC disabled in test, JWT is null → 401 Unauthorized
        RestAssured.given()
            .queryParam("entityType", "workout_sessions")
            .when().get("/api/sync/pull")
            .then()
            .statusCode(401);
    }

    @Test
    public void testHealthEndpoint() {
        // Health endpoint does not require JWT
        RestAssured.given()
            .when().get("/api/sync/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }
}
