package me.senseiju.cosmo_plugin.utils.extensions

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player

private val protocolManager = ProtocolLibrary.getProtocolManager()

/**
 * Send packet to a single player
 *
 * @param player the player
 */
fun PacketContainer.sendPacket(player: Player) {
    protocolManager.sendServerPacket(player, this)
}

/**
 * Send packet to all players
 *
 * @param players the players
 */
fun PacketContainer.broadcastPacket(players: Collection<Player>) {
    players.forEach { sendPacket(it) }
}