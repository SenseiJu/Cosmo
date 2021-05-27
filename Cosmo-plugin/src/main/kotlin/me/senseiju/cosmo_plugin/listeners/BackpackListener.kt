package me.senseiju.cosmo_plugin.listeners

import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.utils.extensions.broadcastPacket
import me.senseiju.cosmo_plugin.packets.createDestroyEntityPacket
import me.senseiju.cosmo_plugin.packets.createMountEntityPacket
import me.senseiju.cosmo_plugin.packets.createRotateEntityHeadPacket
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Pose
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPoseChangeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import java.util.*
import kotlin.collections.HashMap

val playerBackpackArmorStand = HashMap<UUID, ArmorStand>()

class BackpackListener(plugin: Cosmo) : Listener {
    private val modelManager = plugin.modelManager

    @EventHandler
    private fun playerResourcePackStatus(e: PlayerResourcePackStatusEvent) {
        if (e.status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            playerBackpackArmorStand[e.player.uniqueId] = createNewBackpackArmorStand(e.player)

            modelManager.updateModelsToActivePlayers(e.player)
        }
    }

    @EventHandler
    private fun onPlayerChangePose(e: EntityPoseChangeEvent) {
        if (e.entityType != EntityType.PLAYER) return

        val player = e.entity as Player
        val stand = playerBackpackArmorStand[player.uniqueId] ?: return

        when (e.pose) {
            Pose.STANDING, Pose.SNEAKING -> modelManager.updateModelsToActivePlayers(player)
            else -> createDestroyEntityPacket(stand).broadcastPacket(modelManager.playersWithPack)
        }
    }

    @EventHandler
    private fun onPlayerMove(e: PlayerMoveEvent) {
        val stand = playerBackpackArmorStand[e.player.uniqueId] ?: return
        val yaw = e.player.eyeLocation.yaw * 256.0f / 360.0f

        stand.teleport(e.player.location.add(0.0,  1.2, 0.0))

        createMountEntityPacket(e.player, stand)
        createRotateEntityHeadPacket(stand, yaw)
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        playerBackpackArmorStand.remove(e.player.uniqueId)?.remove()
    }
}

fun createNewBackpackArmorStand(player: Player): ArmorStand {
    val stand = player.world.spawnEntity(player.location, EntityType.ARMOR_STAND) as ArmorStand

    stand.isInvisible = true
    stand.isInvulnerable = true
    stand.isVisible = false
    stand.isMarker = true

    return stand
}