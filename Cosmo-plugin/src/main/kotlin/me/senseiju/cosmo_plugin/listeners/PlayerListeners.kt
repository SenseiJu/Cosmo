package me.senseiju.cosmo_plugin.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import me.senseiju.cosmo_plugin.ModelManager
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import us.myles.ViaVersion.api.Via

class PlayerListeners(private val modelManager: ModelManager) : Listener {
    private val viaApi = try {
        Via.getAPI()
    } catch (e: NoClassDefFoundError) {
        null
    }

    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent) {
        if (viaApi != null && viaApi.getPlayerVersion(e.player) != 754) {
            return
        }

        /*
        e.player.setResourcePack(
            "${modelManager.url}/api/packs/${modelManager.packId}?type=zip",
            modelManager.packSha1
        )

         */

        e.player.setResourcePack(
            "http://localhost:8080/cosmo",
            modelManager.packSha1
        )
    }

    @EventHandler
    private fun playerResourcePackStatus(e: PlayerResourcePackStatusEvent) {
        if (e.status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            modelManager.requestModelsFromActivePlayers(e.player)
            modelManager.playersWithPack.add(e.player)

            if (e.player.gameMode != GameMode.CREATIVE) {
                modelManager.updateModelsToActivePlayers(e.player)
            }
        }
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        modelManager.playersWithPack.remove(e.player)
    }

    @EventHandler
    private fun onWorldChange(e: PlayerChangedWorldEvent) {
        if (e.player.gameMode != GameMode.CREATIVE) {
            modelManager.updateModelsToActivePlayers(e.player)
        }
    }

    @EventHandler
    private fun onHelmetChange(e: PlayerArmorChangeEvent) {
        if (e.player.gameMode == GameMode.CREATIVE) {
            return
        }

        if (e.slotType != PlayerArmorChangeEvent.SlotType.HEAD) {
            return
        }

        if (e.newItem?.type == Material.AIR) {
            e.player.updateInventory()
            return
        }

        modelManager.updateModelsToActivePlayers(e.player)
    }

    @EventHandler
    private fun onGameModeChange(e: PlayerGameModeChangeEvent) {
        if (e.newGameMode == GameMode.CREATIVE) {
            e.player.updateInventory()
        } else {
            modelManager.updateModelsToActivePlayers(e.player)
        }
    }
}