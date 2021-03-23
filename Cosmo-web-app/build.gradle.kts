import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktorVersion = "1.5.2"

repositories {
    maven("https://kotlin.bintray.com/ktor")
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

tasks {
    named<Jar>("jar") {
        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_web_app.MainKt"
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("Cosmo-web-app.jar")

        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_web_app.MainKt"
        }
    }
}