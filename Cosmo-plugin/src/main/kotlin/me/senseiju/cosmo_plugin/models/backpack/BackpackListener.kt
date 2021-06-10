package me.senseiju.cosmo_plugin.models.backpack

import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.ModelManager
import me.senseiju.cosmo_plugin.utils.extensions.broadcastPacket
import me.senseiju.cosmo_plugin.utils.extensions.newRunnable
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityPoseChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

val playerBackpackArmorStand = HashMap<UUID, ArmorStand>()

class BackpackListener(private val plugin: Cosmo, private val modelManager: ModelManager) : Listener {

    @EventHandler
    private fun playerResourcePackStatus(e: PlayerResourcePackStatusEvent) {
        if (e.status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            playerBackpackArmorStand[e.player.uniqueId] = createNewBackpackArmorStand(e.player)
        }
    }

    @EventHandler
    private fun onPlayerChangePose(e: EntityPoseChangeEvent) {
        if (e.entityType != EntityType.PLAYER) return

        val player = e.entity as Player
        val stand = playerBackpackArmorStand[player.uniqueId] ?: return

        when (e.pose) {
            Pose.STANDING, Pose.SNEAKING -> modelManager.updateModelsToActivePlayers(player, ModelType.BACKPACK)
            else -> createDestroyEntityPacket(stand).broadcastPacket(modelManager.playersWithPack)
        }
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        val stand = playerBackpackArmorStand[e.player.uniqueId] ?: return

        stand.teleport(e.player.location.add(0.0, 1.2, 0.0))
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        playerBackpackArmorStand.remove(e.player.uniqueId)?.remove()
    }

    @EventHandler
    private fun onArmorStandRemove(e: EntityDeathEvent) {
        playerBackpackArmorStand.forEach {
            if (it.value != e.entity) {
                return@forEach
            }

            val player = Bukkit.getPlayer(it.key) ?: return@forEach
            if (!player.isOnline) {
                return@forEach
            }

            playerBackpackArmorStand[it.key] = createNewBackpackArmorStand(player)
            modelManager.updateModelsToActivePlayers(player, ModelType.BACKPACK)
        }
    }

    @EventHandler
    private fun onPlayerTeleport(e: PlayerTeleportEvent) {
        newRunnable {
            modelManager.updateModelsToActivePlayers(e.player, ModelType.BACKPACK)
        }.runTaskLater(plugin, 1)
    }
}

private fun createNewBackpackArmorStand(player: Player): ArmorStand {
    val stand = player.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand

    stand.isInvisible = true
    stand.isInvulnerable = true
    stand.isVisible = false
    stand.isMarker = true

    return stand
}