package me.senseiju.cosmo_plugin.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.cosmo_plugin.ModelManager
import me.senseiju.cosmo_plugin.guis.openCosmoGui
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("Cosmo")
class CosmoCommand(private val modelManager: ModelManager) : CommandBase() {

    @Default
    @Permission("cosmo.command.use")
    fun default(player: Player) {
        if (!modelManager.playersWithPack.contains(player)) {
            player.sendConfigMessage("COMMAND-COSMO-PACK-REQUIRED")
            return
        }

        openCosmoGui(player)
    }
}