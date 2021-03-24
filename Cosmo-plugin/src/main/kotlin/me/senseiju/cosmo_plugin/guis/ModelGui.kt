package me.senseiju.cosmo_plugin.guis

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.utils.defaultScope
import me.senseiju.cosmo_plugin.utils.extensions.defaultGuiTemplate
import me.senseiju.cosmo_plugin.utils.extensions.defaultPaginatedGuiTemplate
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(Cosmo::class.java)
private val scheduler = plugin.server.scheduler
private val modelManager = plugin.modelManager

fun openCosmoGui(player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, "Cosmo")

        val helmetGuiItem = ItemBuilder
            .from(getPlayersActiveModelAsItem(player, ModelType.HAT))
            .setName("Hats")
            .asGuiItem {
                if (it.isRightClick) {
                    modelManager.setActiveModel(player.uniqueId, ModelType.HAT, null)
                    return@asGuiItem
                }

                openSelectActiveModelGui(player, ModelType.HAT)
            }

        gui.setItem(2, 5, helmetGuiItem)

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun getPlayersActiveModelAsItem(player: Player, modelType: ModelType): ItemStack {
    return modelManager.getPlayersActiveModel(player, modelType)?.item ?: ItemStack(Material.BARRIER)
}

private fun openSelectActiveModelGui(player: Player, modelType: ModelType) {
    val gui = defaultPaginatedGuiTemplate(6, 45, "Select model")

    gui.setCloseGuiAction {
        openCosmoGui(player)
    }

    modelManager.models[modelType]?.forEach { (modelData, model) ->
        gui.addItem(ItemBuilder.from(model.item).asGuiItem {
            modelManager.setActiveModel(player.uniqueId, modelType, modelData)
        })
    }

    scheduler.runTask(plugin, Runnable { gui.open(player) })
}