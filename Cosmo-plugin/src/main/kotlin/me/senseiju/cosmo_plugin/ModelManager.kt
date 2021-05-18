package me.senseiju.cosmo_plugin

import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.Pair
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.commands.CosmoCommand
import me.senseiju.cosmo_plugin.listeners.PlayerListeners
import me.senseiju.cosmo_plugin.packets.broadcastPacket
import me.senseiju.cosmo_plugin.packets.createPlayServerEntityEquipmentPacket
import me.senseiju.cosmo_plugin.packets.sendPacket
import me.senseiju.cosmo_plugin.utils.datastorage.RawDataFile
import me.senseiju.cosmo_plugin.utils.defaultScope
import me.senseiju.cosmo_plugin.utils.extensions.color
import me.senseiju.cosmo_plugin.utils.extensions.registerEvents
import me.senseiju.cosmo_plugin.utils.extensions.setUserAgent
import me.senseiju.cosmo_plugin.utils.serializers.UUIDSerializer
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

const val CUSTOM_MODEL_DATA_TAG = "CustomModelData"

class ModelManager(private val plugin: Cosmo) {
    val models = hashMapOf<ModelType, HashMap<Int, Model>>()
    val playersWithPack = hashSetOf<Player>()
    val packId = plugin.configFile.config.getString("pack-id", null)
    private val url: String = if (plugin.configFile.config.getBoolean("development-server", false)) {
        "http://dev.cosmo.senseiju.me:8080"
    } else {
        "https://cosmo.senseiju.me"
    }
    var packSha1 = ""
        private set

    private lateinit var playersActiveModels: HashMap<UUID, EnumMap<ModelType, Int>>

    private val activeModelsFile = RawDataFile(plugin, "active-models.json")
    private val kickOnReload = plugin.configFile.config.getBoolean("kick-players-on-reload", true)
    private val logger = plugin.slF4JLogger

    /**
     * Save all players active models
     */
    fun saveActiveModels() {
        activeModelsFile.write(Json.encodeToString(ActivePlayerModels(playersActiveModels)))
    }

    fun arePlayersActiveModelsInitialised(): Boolean {
        return ::playersActiveModels.isInitialized
    }

    /**
     * Load all players active models
     */
    private fun loadActiveModels() {
        val previousPackSHA = plugin.configFile.config.get("previous-pack-hash")
        if (previousPackSHA?.equals(packSha1) == false) {
            plugin.configFile.config.set("previous-pack-hash", packSha1)
            plugin.configFile.save()

            playersActiveModels = HashMap()

            activeModelsFile.write("")

            logger.info("SHA hash for new pack is different to last known hash, clearing previous active player models")

            handleOnlinePlayers(true)
            return
        }

        playersActiveModels = try {
            Json.decodeFromString<ActivePlayerModels>(activeModelsFile.read()).toTypedMap()
        } catch (ex: Exception) {
            HashMap()
        }

        handleOnlinePlayers()
    }

    /**
     * Gets the players current active model
     *
     * @param player the player
     * @param modelType the model type
     *
     * @return the model if active or null
     */
    fun getPlayersActiveModel(player: Player, modelType: ModelType): Model? {
        return models[modelType]?.get(playersActiveModels[player.uniqueId]?.get(modelType))
    }

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
            with(URL("$url/api/packs/$packId?type=json").openConnection() as HttpURLConnection) {
                this.setUserAgent()
                this.connect()

                Json.decodeFromString<List<Model>>(this.inputStream.bufferedReader().readText())
                    .forEach {
                        models.computeIfAbsent(it.modelType) {
                            hashMapOf()
                        }[it.modelData] = it
                    }
            }

            with(URL("$url/api/packs/$packId?type=sha1").openConnection() as HttpURLConnection) {
                this.setUserAgent()
                this.connect()

                packSha1 = this.inputStream.bufferedReader().readText()
            }
        } catch (e: Exception) {
            logger.error("Failed to find a valid resource pack with UUID: $packId")
            logger.error("Check your pack-id and make sure it is correct")

            e.printStackTrace()
            return false
        }

        requestModelsSuccess()

        return true
    }

    /**
     * Downloads the resource pack from the Cosmo CDN
     */
    private fun downloadPackZip() {
        if (!plugin.httpServer.isEnabled) {
            return
        }

        with(URL("$url/api/packs/$packId?type=zip").openConnection() as HttpURLConnection) {
            this.setUserAgent()
            this.connect()

            File(plugin.dataFolder, "pack.zip").writeBytes(this.inputStream.buffered().readBytes())
        }
    }

    /**
     * Send helmet model packet
     *
     * @param player the player
     * @param targets the target players, none to send to all
     */
    private fun sendHelmetModelPacket(player: Player, vararg targets: Player) {
        val packet = createHelmetModelPacket(player) ?: createPlayServerEntityEquipmentPacket(
            player.entityId,
            Pair(EnumWrappers.ItemSlot.HEAD, player.inventory.helmet)
        )

        if (targets.isEmpty()) {
            broadcastPacket(playersWithPack, packet)
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
        val modelData = playersActiveModels[player.uniqueId]?.get(ModelType.HAT) ?: return null
        val modelItem = models[ModelType.HAT]
            ?.get(modelData)?.applyItemToModel(player.inventory.helmet ?: ItemStack(Material.AIR)) ?: return null

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
        downloadPackZip()
        loadActiveModels()
        registerEvents()
        registerCommands()
    }

    /**
     * If server/plugin is reloaded with an external manager, this will re-add all players online back
     * to the [playersWithPack] list so the plugin still recognises them
     */
    private fun handleOnlinePlayers(newPack: Boolean = false) {
        plugin.server.onlinePlayers.forEach {
            if (!it.hasResourcePack()) {
                return@forEach
            }

            if (!newPack) {
                playersWithPack.add(it)
                return@forEach
            }

            if (kickOnReload) {
                it.kick(
                    Component.text(("""&d&lCosmo &8&l» &cYou are using an outdated version of the resource pack, 
                            |reconnect to get the latest
                        """.trimMargin().color())
                    )
                )
            } else {
                it.sendConfigMessage("PACK-OUTDATED")
            }
        }
    }

    /**
     * Register plugin events
     */
    private fun registerEvents() {
        plugin.registerEvents(PlayerListeners(plugin, this))
    }

    /**
     * Register plugin commands
     */
    private fun registerCommands() {
        plugin.commandManager.register(CosmoCommand(this))
    }

    @Serializable
    data class ActivePlayerModels(val models: Map<@Serializable(UUIDSerializer::class) UUID, Map<ModelType, Int>>) {
        fun toTypedMap(): HashMap<UUID, EnumMap<ModelType, Int>> {
            return models.map {
                it.key to it.value.toMap(EnumMap(ModelType::class.java))
            }.toMap(hashMapOf())
        }
    }
}