package me.senseiju.cosmo_plugin.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.cosmo_plugin.ModelManager
import me.senseiju.cosmo_commons.ModelType
import org.bukkit.entity.Player

@Command("Cosmo")
class CosmoCommand(private val modelManager: ModelManager) : CommandBase() {

    @Default
    fun default(player: Player) {
        modelManager.setActiveModel(player.uniqueId, ModelType.HELMET, 100)
    }

    @SubCommand("a")
    fun a(player: Player) {
        println(modelManager.playersWithPack)
    }
}