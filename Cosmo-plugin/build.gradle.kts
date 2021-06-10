import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val version = "0.10.3"

repositories {
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.viaversion.com")
    maven("https://papermc.io/repo/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation("com.comphenix.protocol:ProtocolLib:4.6.0")
    implementation("com.viaversion:viaversion-api:4.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("me.mattstudios.utils:matt-framework-gui:2.0.2")
    implementation("me.mattstudios.utils:matt-framework:1.4.6")
    implementation("de.tr7zw:item-nbt-api:2.7.1")
    implementation("org.bstats:bstats-bukkit:2.2.1")
    implementation("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    withType(ShadowJar::class) {
        archiveFileName.set("Cosmo.jar")

        relocate("me.mattstudios.mfgui", "me.senseiju.shaded.mfgui")
        relocate("me.mattstudios.mf", "me.senseiju.shaded.mf")
        relocate("de.tr7zw.changeme.nbtapi", "me.senseiju.shaded.nbtapi")
        relocate("org.bstats", "me.senseiju.shaded.bstats")

        minimize()
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    register("copyJarToServer", Copy::class) {
        from(shadowJar)
        into("D:/Servers/Minecraft/Cosmo/plugins/update")
        //into("D:/Servers/Minecraft/SennetMC/plugins/update")
    }
}