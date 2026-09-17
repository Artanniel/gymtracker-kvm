package com.gymtracker;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestProfile(CustomTestProfile.class)
public class WorkoutSessionControllerTest {

    @Test
    public void testListSessionsWithoutAuth() {
        // With OIDC disabled in test, JWT is null → 401 Unauthorized
        RestAssured.given()
            .when().get("/api/workout-sessions")
            .then()
            .statusCode(401);
    }

    @Test
    public void testSyncHealthWithoutAuth() {
        // Health endpoint does not require JWT
        RestAssured.given()
            .when().get("/api/sync/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }

    @Test
    public void testCreateSession() {
        // With OIDC disabled in test, JWT is null → 401 Unauthorized
        String sessionJson = """
            {
                "workoutId": "upper-body-push",
                "workoutName": "Push Day",
                "date": 1700000000000,
                "notes": "Test session"
            }
            """;

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(sessionJson)
            .when().post("/api/workout-sessions")
            .then()
            .statusCode(401);
    }
}
