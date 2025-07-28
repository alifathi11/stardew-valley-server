plugins {
    java
    id("org.springframework.boot") version "3.2.5" // or the latest version you're using
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17 // or your preferred version
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // SQLite JDBC Driver
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    // For password hashing (BCrypt)
    implementation("org.springframework.security:spring-security-crypto")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Spring Boot core dependencies
    implementation("org.springframework.boot:spring-boot-starter")

    // JDBC support (javax.sql.DataSource and java.sql.Connection)
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // (Optional) In-memory database for testing or quick dev
    runtimeOnly("com.h2database:h2") // or use PostgreSQL / MySQL driver

    // Kotlin support
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
    implementation("com.badlogicgames.gdx:gdx:1.12.1")

    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
}

tasks.test {
    useJUnitPlatform()
}
