import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    compileOnly("org.litote.kmongo:kmongo:4.2.5")
}

tasks {
    "shadowJar"(ShadowJar::class) {
        archiveFileName.set("Cosmo-pack-builder.jar")

        manifest {
            attributes["Main-Class"] = "me.senseiju.cosmo_pack_builder.MainKt"
        }

        minimize()
    }
}