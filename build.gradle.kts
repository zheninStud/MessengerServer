plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.stanley"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.json:json:20231013")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "ru.stanley.Server"
        )
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes(
            "Main-Class" to "ru.stanley.Server"
        )
    }
}