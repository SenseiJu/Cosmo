package me.senseiju.cosmo_plugin.models.hat

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


fun createEntityEquipmentPacket(
    player: Player,
    vararg slotItemPairs: Pair<EnumWrappers.ItemSlot, ItemStack>
): PacketContainer {
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT)
    packet.integers.write(0, player.entityId)
    packet.slotStackPairLists.write(0, listOf(*slotItemPairs))

    return packet
}