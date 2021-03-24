plugins {
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.serialization") version "1.4.10"

    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.senseiju"
version = "0.0.1"

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
        implementation("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

        testImplementation(kotlin("test-junit5"))
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    }
}

/**
 * All projects, excludes Cosmo-commons
 */
configure(allprojects - project(":Cosmo-commons")) {
    dependencies {
        implementation(project(":Cosmo-commons"))
    }
}