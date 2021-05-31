package me.senseiju.cosmo_plugin

import com.codingforcookies.armorequip.ArmorEquip
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mfgui.gui.guis.BaseGui
import me.senseiju.cosmo_plugin.http.InternalHttpServer
import me.senseiju.cosmo_plugin.models.backpack.playerBackpackArmorStand
import me.senseiju.cosmo_plugin.utils.datastorage.DataFile
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin


class Cosmo : JavaPlugin() {
    var debugMode = false

    lateinit var commandManager: CommandManager
    lateinit var modelManager: ModelManager
        private set
    lateinit var configFile: DataFile
        private set
    lateinit var messagesFile: DataFile
        private set
    lateinit var httpServer: InternalHttpServer

    override fun onEnable() {
        reload()

        ArmorEquip(this)
        Metrics(this, 10975)
    }

    override fun onDisable() {
        httpServer.stop()

        cleanUp()
    }

    private fun cleanUp() {
        if (modelManager.arePlayersActiveModelsInitialised()) {
            modelManager.saveActiveModels()
        }

        server.onlinePlayers.forEach {
            if (it.openInventory.topInventory.holder is BaseGui) {
                it.closeInventory()
            }
        }

        playerBackpackArmorStand.values.forEach { it.remove() }
    }

    fun reload() {
        configFile = DataFile(this, "config.yml", true)
        messagesFile = DataFile(this, "messages.yml", true)

        if (::modelManager.isInitialized) {
            cleanUp()
        } else {
            modelManager = ModelManager(this)
        }
        if (!::httpServer.isInitialized) {
            httpServer = InternalHttpServer(this)
        }
        if (!::commandManager.isInitialized) {
            commandManager = CommandManager(this)
        }

        modelManager.packId = configFile.config.getString("pack-id", "a939f783-3711-48ee-b2f5-69f66efcd1c2")!!
        if (!modelManager.requestModelsJson()) {
            server.pluginManager.disablePlugin(this)
            return
        }
    }
}