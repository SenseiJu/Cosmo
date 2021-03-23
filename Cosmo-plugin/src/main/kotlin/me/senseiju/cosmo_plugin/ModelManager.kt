package me.senseiju.cosmo_plugin

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.commands.CosmoCommand
import me.senseiju.cosmo_plugin.listeners.PlayerListeners
import me.senseiju.cosmo_plugin.packets.broadcastPacket
import me.senseiju.cosmo_plugin.packets.createPlayServerEntityEquipmentPacket
import me.senseiju.cosmo_plugin.packets.sendPacket
import me.senseiju.cosmo_plugin.utils.defaultScope
import me.senseiju.cosmo_plugin.utils.extensions.registerEvents
import org.bukkit.entity.Player
import java.net.URL
import java.util.*

class ModelManager(private val plugin: Cosmo) {
    val playersWithPack = hashSetOf<Player>()
    val resourcePackUUID = plugin.configFile.config.getString("resource-pack-uuid", null)
    var resourcePackSHA1Digest = ""
        private set

    private val logger = plugin.slF4JLogger
    private val models = hashMapOf<ModelType, HashMap<Int, Model>>()
    private val playersActiveModels = hashMapOf<UUID, EnumMap<ModelType, Int>>()
    private val protocolManager = ProtocolLibrary.getProtocolManager()

    /**
     * Set a players active model for a specified model type and model data
     *
     * @param uuid the players uuid
     * @param modelType the model type
     * @param modelData the model data, null to remove
     */
    fun setActiveModel(uuid: UUID, modelType: ModelType, modelData: Int? = null) {
        if (modelData == null) {
            playersActiveModels[uuid]?.remove(modelType)
            return
        }

        playersActiveModels.computeIfAbsent(uuid) {
            EnumMap(ModelType::class.java)
        }[modelType] = modelData
    }

    /**
     * Updates all players models if they have the resource pack active
     *
     * @param player the player
     */
    fun updateModelsToActivePlayers(player: Player) {
        defaultScope.launch {
            if (!playersWithPack.contains(player)) {
                return@launch
            }

            sendHelmetModelPacket(player)
            //sendBackpackModelPacket()
        }
    }

    /**
     * Request models from players with resource pack active
     *
     * @param player the player
     */
    fun requestModelsFromActivePlayers(player: Player) {
        playersWithPack.forEach {
            sendHelmetModelPacket(it, player)
        }
    }

    /**
     * Get custom resource pack model data by given UUID.
     * Calls [requestModelsSuccess] if successful
     *
     * @return true if successful
     */
    fun requestModelsJson(): Boolean {
        try {
            Json.decodeFromString<List<Model>>(URL("http://cosmo.senseiju.me:8080/$resourcePackUUID").readText())
                .forEach {
                    models.computeIfAbsent(it.modelType) {
                        hashMapOf()
                    }[it.modelData] = it
                }

            resourcePackSHA1Digest = URL("http://cosmo.senseiju.me:8080/${resourcePackUUID}?digest=1").readText()
        } catch (e: Exception) {
            logger.error("Failed to find a valid resource pack with UUID: $resourcePackUUID")

            return false
        }

        requestModelsSuccess()

        return true
    }

    /**
     * Send helmet model packet
     *
     * @param player the player
     * @param targets the target players, none to send to all
     */
    private fun sendHelmetModelPacket(player: Player, vararg targets: Player) {
        val packet = createHelmetModelPacket(player) ?: return

        if (targets.isEmpty()) {
            broadcastPacket(playersWithPack ,packet)
        } else {
            targets.forEach {
                sendPacket(it, packet)
            }
        }
    }

    /**
     * Create helmet model packet
     *
     * @param player the player
     *
     * @return a packet or null if a packet cannot be created
     */
    private fun createHelmetModelPacket(player: Player): PacketContainer? {
        val modelData = playersActiveModels[player.uniqueId]?.get(ModelType.HELMET) ?: return null
        val modelItem = models[ModelType.HELMET]?.get(modelData)?.applyItemToModel(player.inventory.helmet!!) ?: return null

        val slotItemPair = Pair(EnumWrappers.ItemSlot.HEAD, modelItem)

        return createPlayServerEntityEquipmentPacket(player.entityId, slotItemPair)
    }

    /**
     * Send backpack model packet
     */
    private fun sendBackpackModelPacket() {
        //todo: Add when backpacks are introduced
    }

    /**
     * Called if initial requests for models succeeds
     */
    private fun requestModelsSuccess() {
        registerEvents()
        registerCommands()
    }

    /**
     * Register plugin events
     */
    private fun registerEvents() {
        plugin.registerEvents(PlayerListeners(this))
    }

    /**
     * Register plugin commands
     */
    private fun registerCommands() {
        plugin.commandManager.register(CosmoCommand(this))
    }
}