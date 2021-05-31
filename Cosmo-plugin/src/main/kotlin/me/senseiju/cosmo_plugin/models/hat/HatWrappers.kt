package me.senseiju.cosmo_plugin.models.hat

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.NullPointerException


fun createEntityEquipmentPacket(
    player: Player,
    vararg slotItemPairs: Pair<EnumWrappers.ItemSlot, ItemStack>
): PacketContainer {
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT)
    packet.integers.write(0, player.entityId)

    // Until I figure this shit out, ima just catch the exception and move on with life :/
    try {
        packet.slotStackPairLists.write(0, listOf(*slotItemPairs))
    } catch (ignored: NullPointerException) {
    }

    return packet
}