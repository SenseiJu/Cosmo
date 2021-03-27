import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    "jar"(Jar::class) {
        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_web_app.MainKt"
        }
    }

    "shadowJar"(ShadowJar::class) {
        archiveFileName.set("Cosmo-web-app.jar")

        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_web_app.MainKt"
        }

        minimize()
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}