package me.senseiju.cosmo_plugin

import me.mattstudios.mf.base.CommandManager
import me.senseiju.cosmo_plugin.utils.datastorage.DataFile
import org.bukkit.plugin.java.JavaPlugin


class Cosmo : JavaPlugin() {
    val configFile = DataFile(this, "config.yml", true)
    val messagesFile = DataFile(this, "messages.yml", true)

    lateinit var commandManager: CommandManager
    lateinit var modelManager: ModelManager

    override fun onEnable() {
        commandManager = CommandManager(this)
        modelManager = ModelManager(this)

        if (!modelManager.requestModelsJson()) {
            server.pluginManager.disablePlugin(this)
            return
        }
    }

    override fun onDisable() {
        modelManager.saveActiveModels()
    }
}