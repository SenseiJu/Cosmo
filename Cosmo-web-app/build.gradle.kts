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
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("software.amazon.awssdk:cognitoidentityprovider:2.16.30")

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