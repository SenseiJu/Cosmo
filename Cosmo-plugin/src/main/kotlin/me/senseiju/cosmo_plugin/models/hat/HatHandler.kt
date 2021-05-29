package me.senseiju.cosmo_plugin.models.hat

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.ModelManager
import me.senseiju.cosmo_plugin.models.ModelHandlerImpl
import me.senseiju.cosmo_plugin.utils.extensions.broadcastPacket
import me.senseiju.cosmo_plugin.utils.extensions.registerEvents
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private val MODEL_TYPE = ModelType.HAT

class HatHandler(plugin: Cosmo, private val modelManager: ModelManager) : ModelHandlerImpl {
    private val requireHelmets = plugin.configFile.config.getBoolean("require-helmets", false)

    init {
        plugin.registerEvents(HatListener(plugin, modelManager))
    }

    override fun update(player: Player, targets: Collection<Player>) {
        val packet = createHelmetModelPacket(player)
            ?: createEntityEquipmentPacket(player, Pair(EnumWrappers.ItemSlot.HEAD, player.inventory.helmet))

        packet.broadcastPacket(targets)
    }

    private fun createHelmetModelPacket(player: Player): PacketContainer? {
        if (requireHelmets && player.inventory.helmet == null) {
            return null
        }

        val modelData = modelManager.playersActiveModels[player.uniqueId]?.get(MODEL_TYPE) ?: return null
        val modelItem = modelManager.models[MODEL_TYPE]
            ?.get(modelData)?.applyItemToModel(player.inventory.helmet ?: ItemStack(Material.AIR)) ?: return null

        val slotItemPair = Pair(EnumWrappers.ItemSlot.HEAD, modelItem)

        return createEntityEquipmentPacket(player, slotItemPair)
    }
}