import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks {
    "shadowJar"(ShadowJar::class) {
        archiveFileName.set("Cosmo-pack-builder.jar")

        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_pack_builder.MainKt"
        }

        minimize()
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("net.lingala.zip4j:zip4j:2.7.0")
    implementation("commons-codec:commons-codec:1.9")
}
repositories {
    mavenCentral()
}