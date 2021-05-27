package me.senseiju.cosmo_plugin.listeners

import me.senseiju.cosmo_plugin.Cosmo
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import us.myles.ViaVersion.api.Via

class PlayerListener(private val plugin: Cosmo) : Listener {
    private val modelManager = plugin.modelManager
    private val httpServer = plugin.httpServer
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

        if (httpServer.isEnabled) {
            e.player.setResourcePack(
                "http://${httpServer.ip.trimIndent()}:${httpServer.port}/cosmo",
                modelManager.packSha1
            )
        } else {
            e.player.setResourcePack(
                "http://cosmo.senseiju.me:8080/api/packs/${modelManager.packId}?type=zip",
                modelManager.packSha1
            )
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
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
    private fun onWorldChange(e: PlayerChangedWorldEvent) {
        modelManager.updateModelsToActivePlayers(e.player)
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        modelManager.playersWithPack.remove(e.player)
    }

    @EventHandler
    private fun onGameModeChange(e: PlayerGameModeChangeEvent) {
        if (e.newGameMode == GameMode.CREATIVE) {
            e.player.updateInventory()
        } else {
            plugin.server.scheduler.runTaskLater(plugin, Runnable { modelManager.updateModelsToActivePlayers(e.player) }, 1L)
        }
    }
}