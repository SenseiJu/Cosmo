package me.senseiju.cosmo_plugin.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.senseiju.cosmo_plugin.Cosmo
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerItemConsumeEvent

class HatListener(private val plugin: Cosmo) : Listener {
    private val modelManager = plugin.modelManager

    @EventHandler
    private fun onHelmetChange(e: PlayerArmorChangeEvent) {
        if (e.slotType != PlayerArmorChangeEvent.SlotType.HEAD) {
            return
        }

        modelManager.updateModelsToActivePlayers(e.player)
    }

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