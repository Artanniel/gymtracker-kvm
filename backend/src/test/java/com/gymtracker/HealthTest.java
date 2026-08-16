package com.gymtracker;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestProfile(CustomTestProfile.class)
public class HealthTest {

    @Test
    public void testHealthEndpoint() {
        RestAssured.given()
            .when().get("/q/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }

    @Test
    public void testSyncHealthEndpoint() {
        RestAssured.given()
            .when().get("/api/sync/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("service", equalTo("gymtracker-backend"));
    }
}
