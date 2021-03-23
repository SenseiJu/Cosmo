import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    implementation("com.comphenix.protocol:ProtocolLib:4.6.0")
    implementation("me.mattstudios.utils:matt-framework-gui:2.0.2")
    implementation("me.mattstudios.utils:matt-framework:1.4.6")
    implementation("de.tr7zw:item-nbt-api:2.7.1")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set("Cosmo.jar")

        relocate("me.mattstudios.mfgui", "me.senseiju.shaded.mfgui")
        relocate("me.mattstudios.mf", "me.senseiju.shaded.mf")
        relocate("de.tr7zw.changeme.nbtapi", "me.senseiju.shaded.nbtapi")

        minimize()
    }
}