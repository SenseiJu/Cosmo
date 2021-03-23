package me.senseiju.cosmo_plugin.packets

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player

private val protocolManager = ProtocolLibrary.getProtocolManager()

/**
 * Send packets to a single target player
 *
 * @param target the target player
 * @param packets the packets to send
 */
fun sendPacket(target: Player, vararg packets: PacketContainer) {
    packets.forEach { packet ->
        protocolManager.sendServerPacket(target, packet)
    }
}

/**
 * Send packets to all players
 *
 * @param packets the packets to send
 */
fun broadcastPacket(players: Collection<Player>, vararg packets: PacketContainer) {
    players.forEach {
        sendPacket(it, *packets)
    }
}