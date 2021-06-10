package me.senseiju.cosmo_plugin.models.hat

import com.codingforcookies.armorequip.ArmorEquipEvent
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.ModelManager
import me.senseiju.cosmo_plugin.usingPaperApi
import me.senseiju.cosmo_plugin.utils.extensions.newRunnable
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerItemConsumeEvent

class HatListener(private val plugin: Cosmo, private val modelManager: ModelManager) : Listener {

    @EventHandler
    private fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        val player = if (e.entity is Player) {
            e.entity as Player
        } else return

        newRunnable {
            modelManager.updateModelsToActivePlayers(player, ModelType.HAT)
        }.runTaskLater(plugin, 1)
    }

    @EventHandler
    private fun onPlayerEat(e: PlayerItemConsumeEvent) {
        modelManager.updateModelsToActivePlayers(e.player, ModelType.HAT)
    }

    @EventHandler
    private fun onProjectileLaunch(e: ProjectileLaunchEvent) {
        if (usingPaperApi) {
            return
        }

        val player = if (e.entity.shooter !is Player) {
            e.entity.shooter as Player
        } else return

        newRunnable {
            modelManager.updateModelsToActivePlayers(player, ModelType.HAT)
        }.runTaskLater(plugin, 1)
    }

    @EventHandler
    private fun onProjectileLaunch(e: PlayerLaunchProjectileEvent) {
        newRunnable {
            modelManager.updateModelsToActivePlayers(e.player, ModelType.HAT)
        }.runTaskLater(plugin, 1)
    }

    @EventHandler
    private fun onHelmetClick(e: InventoryClickEvent) {
        if (e.slotType != InventoryType.SlotType.ARMOR) {
            return
        }

        if (e.slot != 39) {
            return
        }

        modelManager.updateModelsToActivePlayers(e.whoClicked as Player, ModelType.HAT)
    }

    @EventHandler
    private fun onGameModeChange(e: PlayerGameModeChangeEvent) {
        if (e.newGameMode == GameMode.CREATIVE) {
            e.player.updateInventory()
        } else {
            newRunnable {
                modelManager.updateModelsToActivePlayers(e.player, ModelType.HAT)
            }.runTaskLater(plugin, 1)
        }
    }

    @EventHandler
    private fun onArmorChange(e: ArmorEquipEvent) {
        newRunnable {
            modelManager.updateModelsToActivePlayers(e.player, ModelType.HAT)
        }.runTaskLater(plugin, 2)
    }
}