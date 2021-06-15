package me.senseiju.cosmo_plugin.guis

import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.utils.ColorScheme
import me.senseiju.cosmo_plugin.utils.extensions.*
import me.senseiju.sennetmc.utils.extensions.color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

private val plugin = JavaPlugin.getPlugin(Cosmo::class.java)
private val logger = plugin.logger
private val modelManager = plugin.modelManager

fun openCosmoGui(player: Player) {
    if (plugin.debugMode) logger.debug("Creating gui for ${player.name}")

    val gui = defaultGuiTemplate(3, "${ColorScheme.PRIMARY}&lCosmo")

    val modelLore = listOf(
        "",
        "&aLeft-click &7to select a different model",
        "&aRight-click &7to remove your current model"
    )

    val helmetGuiItem = ItemBuilder
        .from(getPlayersActiveModelAsItem(player, ModelType.HAT))
        .setName("${ColorScheme.SECONDARY}Hats".color())
        .setLore(modelLore.color())
        .asGuiItem {
            if (it.isRightClick) {
                modelManager.setActiveModel(player.uniqueId, ModelType.HAT, null)
                modelManager.updateModelsToActivePlayers(player, ModelType.HAT)

                openCosmoGui(player)
                return@asGuiItem
            }

            openSelectActiveModelGui(player, ModelType.HAT)
        }
    if (plugin.debugMode) logger.debug("Created helmet gui item for ${player.name}")

    val backpackGuiItem = ItemBuilder
        .from(getPlayersActiveModelAsItem(player, ModelType.BACKPACK))
        .setName("${ColorScheme.SECONDARY}Backpacks".color())
        .setLore(modelLore.color())
        .asGuiItem {
            if (it.isRightClick) {
                modelManager.setActiveModel(player.uniqueId, ModelType.BACKPACK, null)
                modelManager.updateModelsToActivePlayers(player, ModelType.BACKPACK)

                openCosmoGui(player)
                return@asGuiItem
            }

            openSelectActiveModelGui(player, ModelType.BACKPACK)
        }
    if (plugin.debugMode) logger.debug("Created backpack gui item for ${player.name}")

    gui.setItem(2, 4, helmetGuiItem)
    gui.setItem(2, 6, backpackGuiItem)

    gui.open(player)

    if (plugin.debugMode) logger.debug("Opened gui for ${player.name}")
}

private fun getPlayersActiveModelAsItem(player: Player, modelType: ModelType): ItemStack {
    return modelManager.getPlayersActiveModel(player, modelType)?.item?.clone() ?: ItemStack(Material.BARRIER)
}

private fun openSelectActiveModelGui(player: Player, modelType: ModelType) {
    val gui = defaultPaginatedGuiTemplate(6, 45, "${ColorScheme.PRIMARY}&lSelect model")

    gui.setCloseGuiAction {
        newRunnable { openCosmoGui(player) }.runTaskLater(plugin, 1L)
    }

    modelManager.models[modelType]?.forEach { (modelData, model) ->
        gui.addItem(ItemBuilder.from(model.item).asGuiItem {
            modelManager.setActiveModel(player.uniqueId, modelType, modelData)
            modelManager.updateModelsToActivePlayers(player, modelType)
        })
    }

    gui.open(player)
}