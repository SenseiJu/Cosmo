package me.senseiju.cosmo_plugin

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.senseiju.cosmo_plugin.utils.datastorage.DataFile
import org.bukkit.event.inventory.InventoryCloseEvent
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
        if (modelManager.arePlayersActiveModelsInitialised()) {
            modelManager.saveActiveModels()
        }

        server.onlinePlayers.forEach {
            if (it.openInventory.topInventory.holder is BaseGui) {
                it.closeInventory()
            }
        }
    }
}