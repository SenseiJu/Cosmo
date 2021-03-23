package me.senseiju.cosmo_plugin.utils.datastorage

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class DataFile(private val plugin: JavaPlugin, private val path: String, private val hasDefault: Boolean = false) {
    private lateinit var file: File

    lateinit var config: YamlConfiguration
        private set

    init {
        reload()
    }

    fun reload() {
        file = File("${plugin.dataFolder}/${path}")

        if (!file.exists()) {
            plugin.dataFolder.mkdirs()
            if (hasDefault) {
                plugin.saveResource(path, false)
            } else {
                file.createNewFile()
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun save() = config.save(file)
}