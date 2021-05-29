package me.senseiju.cosmo_plugin.models.backpack

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

fun createRotateEntityHeadPacket(armorStand: ArmorStand, headYaw: Float): PacketContainer {
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION)
    packet.integers.write(0, armorStand.entityId)
    packet.bytes.write(0, headYaw.toInt().toByte())

    return packet
}

fun createDestroyEntityPacket(armorStand: ArmorStand): PacketContainer {
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
    packet.integerArrays.write(0, IntArray(1) { armorStand.entityId })

    return packet
}

fun createMountEntityPacket(player: Player, armorStand: ArmorStand): PacketContainer {
    val packet = PacketContainer(PacketType.Play.Server.MOUNT)
    packet.integers.write(0, player.entityId)
    packet.integerArrays.write(0, IntArray(1) { armorStand.entityId })

    return packet
}