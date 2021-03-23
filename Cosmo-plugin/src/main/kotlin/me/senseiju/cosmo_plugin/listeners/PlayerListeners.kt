package me.senseiju.cosmo_plugin.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import kotlinx.coroutines.launch
import me.senseiju.cosmo_plugin.ModelManager
import me.senseiju.cosmo_plugin.utils.defaultScope
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*

class PlayerListeners(private val modelManager: ModelManager) : Listener {

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.setResourcePack(
            "http://86.172.51.14:8080/${modelManager.resourcePackUUID}?dl=1",
            modelManager.resourcePackSHA1Digest)
    }

    @EventHandler
    private fun playerResourcePackStatus(e: PlayerResourcePackStatusEvent) {
        if (e.status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            modelManager.playersWithPack.add(e.player)
            modelManager.updateModelsToActivePlayers(e.player)
            modelManager.requestModelsFromActivePlayers(e.player)
        }
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        modelManager.playersWithPack.remove(e.player)
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerChangedWorldEvent) {
        modelManager.updateModelsToActivePlayers(e.player)
    }

    @EventHandler
    private fun onHelmetChange(e: PlayerArmorChangeEvent) {
        defaultScope.launch {
            if (e.slotType != PlayerArmorChangeEvent.SlotType.HEAD) {
                return@launch
            }

            if (e.newItem?.type == Material.AIR) {
                return@launch
            }

            modelManager.updateModelsToActivePlayers(e.player)
        }
    }
}