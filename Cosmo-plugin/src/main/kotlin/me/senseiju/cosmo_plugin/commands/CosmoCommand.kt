package me.senseiju.cosmo_plugin.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.ModelManager
import me.senseiju.cosmo_plugin.guis.openCosmoGui
import me.senseiju.cosmo_plugin.utils.extensions.color
import me.senseiju.cosmo_plugin.utils.extensions.debug
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

@Command("Cosmo")
class CosmoCommand(private val plugin: Cosmo, private val modelManager: ModelManager) : CommandBase() {
    private val logger = plugin.logger

    @Default
    @Permission("cosmo.command.use")
    fun default(player: Player) {
        if (!modelManager.playersWithPack.contains(player)) {
            player.sendConfigMessage("COMMAND-COSMO-PACK-REQUIRED")
            return
        }

        if (plugin.debugMode) logger.debug("Opening gui through command /cosmo")

        openCosmoGui(player)
    }

    @SubCommand("debug")
    @Permission("cosmo.command.debug")
    fun debug(sender: CommandSender) {
        plugin.debugMode = !plugin.debugMode

        if (plugin.debugMode) {
            sender.sendConfigMessage("DEBUG-MODE-ENABLED")
        } else {
            sender.sendConfigMessage("DEBUG-MODE-DISABLED")
        }
    }

    @SubCommand("reload")
    @Permission("cosmo.command.reload")
    fun reload(sender: ConsoleCommandSender) {
        logger.info("&aReloading Cosmo...".color())
        plugin.reload()
        logger.info("&aReload complete!".color())
    }
}