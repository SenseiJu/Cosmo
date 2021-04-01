import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktorVersion = "1.5.2"

repositories {
    maven("https://kotlin.bintray.com/ktor")
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    withType(Jar::class) {
        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_web_app.MainKt"
        }
    }

    withType(ShadowJar::class) {
        archiveFileName.set("Cosmo-web-app.jar")

        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_web_app.MainKt"
        }

        minimize()
    }

    withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
        kotlinOptions {
            jvmTarget = "1.8"
        }

    }
}