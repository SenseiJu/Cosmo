package me.senseiju.cosmo_plugin.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import org.bukkit.inventory.ItemStack


fun createPlayServerEntityEquipmentPacket(
    entityId: Int,
    vararg slotItemPairs: Pair<EnumWrappers.ItemSlot, ItemStack>
): PacketContainer {
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT)
    packet.integers.write(0, entityId)
    packet.slotStackPairLists.write(0, listOf(*slotItemPairs))

    return packet
}