package me.senseiju.cosmo_plugin.models.backpack

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.ModelManager
import me.senseiju.cosmo_plugin.models.ModelHandlerImpl
import me.senseiju.cosmo_plugin.utils.extensions.broadcastPacket
import me.senseiju.cosmo_plugin.utils.extensions.registerEvents
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

private val MODEL_TYPE = ModelType.BACKPACK

class BackpackHandler(plugin: Cosmo, private val modelManager: ModelManager) : ModelHandlerImpl {
    private val protocolManager = ProtocolLibrary.getProtocolManager()

    init {
        plugin.registerEvents(BackpackListener(plugin, modelManager))
    }

    override fun update(player: Player, targets: Collection<Player>) {
        val armorStand = playerBackpackArmorStand[player.uniqueId] ?: return
        val packet = createBackpackModelPacket(player, armorStand)
            ?: createDestroyEntityPacket(armorStand)

        packet.broadcastPacket(targets)
    }

    private fun createBackpackModelPacket(player: Player, armorStand: ArmorStand): PacketContainer? {
        val modelData = modelManager.playersActiveModels[player.uniqueId]?.get(MODEL_TYPE) ?: return null
        val modelItem = modelManager.models[MODEL_TYPE]?.get(modelData)?.item ?: return null

        armorStand.equipment?.helmet = modelItem

        return createMountEntityPacket(player, armorStand)
    }

    fun updateBackpackEntity(player: Player) {
        with (playerBackpackArmorStand[player.uniqueId] ?: return) {
            this.teleport(player.location.add(0.0, 1.2, 0.0))

            protocolManager.updateEntity(this, player.server.onlinePlayers.toMutableList())
        }
    }

}