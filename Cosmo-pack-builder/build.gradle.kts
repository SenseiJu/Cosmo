import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set("Cosmo-pack-builder.jar")

        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_pack_builder.MainKt"
        }
    }
}