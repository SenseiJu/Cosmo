package me.senseiju.cosmo_plugin.models.hat

import com.codingforcookies.armorequip.ArmorEquipEvent
import com.codingforcookies.armorequip.ArmorType
import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.ModelManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerItemConsumeEvent

class HatListener(private val plugin: Cosmo, private val modelManager: ModelManager) : Listener {

    @EventHandler
    private fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        val player = if (e.entity is Player) {
            e.entity as Player
        } else return

        plugin.server.scheduler.runTaskLater(
            plugin,
            Runnable { modelManager.updateModelsToActivePlayers(player) },
            2
        )
    }

    @EventHandler
    private fun onPlayerEat(e: PlayerItemConsumeEvent) {
        modelManager.updateModelsToActivePlayers(e.player)
    }

    @EventHandler
    private fun onProjectileLaunch(e: ProjectileLaunchEvent) {
        if (e.entity.shooter !is Player) {
           return
        }

        modelManager.updateModelsToActivePlayers(e.entity.shooter as Player)
    }

    @EventHandler
    private fun onHelmetClick(e: InventoryClickEvent) {
        if (e.slotType != InventoryType.SlotType.ARMOR) {
            return
        }

        if (e.slot != 39) {
            return
        }

        modelManager.updateModelsToActivePlayers(e.whoClicked as Player)
    }
}