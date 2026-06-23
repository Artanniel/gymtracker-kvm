plugins {
    kotlin("jvm")
    kotlin("plugin.allopen") version "2.1.20"
    alias(libs.plugins.quarkus)
}

dependencies {
    implementation(project(":shared"))
    
    implementation(platform("io.quarkus.platform:quarkus-bom:3.15.1"))
    
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    
    // Panache and PostgreSQL
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    
    // OIDC for Google Auth
    implementation("io.quarkus:quarkus-oidc")
    
    // Kotlin reflection
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

group = "com.gymtracker"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}
