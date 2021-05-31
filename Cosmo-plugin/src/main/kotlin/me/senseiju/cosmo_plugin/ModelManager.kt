package me.senseiju.cosmo_plugin

import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.commands.CosmoCommand
import me.senseiju.cosmo_plugin.listeners.PlayerListener
import me.senseiju.cosmo_plugin.models.backpack.BackpackHandler
import me.senseiju.cosmo_plugin.models.hat.HatHandler
import me.senseiju.cosmo_plugin.utils.datastorage.RawDataFile
import me.senseiju.cosmo_plugin.utils.defaultScope
import me.senseiju.cosmo_plugin.utils.extensions.registerEvents
import me.senseiju.cosmo_plugin.utils.extensions.setUserAgent
import me.senseiju.cosmo_plugin.utils.serializers.UUIDSerializer
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

const val CUSTOM_MODEL_DATA_TAG = "CustomModelData"

class ModelManager(private val plugin: Cosmo) {
    val models = hashMapOf<ModelType, HashMap<Int, Model>>()
    val playersWithPack = hashSetOf<Player>()
    lateinit var packId: String
    private val url: String = if (plugin.configFile.config.getBoolean("development-server", false)) {
        "http://dev.cosmo.senseiju.me:8080"
    } else {
        "https://cosmo.senseiju.me"
    }
    private var packSha1 = ""

    lateinit var playersActiveModels: HashMap<UUID, EnumMap<ModelType, Int>>

    private val activeModelsFile = RawDataFile(plugin, "active-models.json")
    private val logger = plugin.logger
    private val hatHandler = HatHandler(plugin, this)
    private val backpackHandler = BackpackHandler(plugin, this)

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
        val hashFile = RawDataFile(plugin, "pack.sha1")
        val previousPackSHA = hashFile.read()
        if (previousPackSHA != packSha1) {
            hashFile.write(packSha1)

            playersActiveModels = HashMap()

            activeModelsFile.write("")

            logger.info("SHA hash for new pack is different to last known hash, clearing previous active player models")

            handleOnlinePlayers()
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
        if (!playersWithPack.contains(player)) {
            return
        }

        backpackHandler.updateBackpackEntity(player)

        defaultScope.launch {
            if (player.gameMode != GameMode.CREATIVE) {
                hatHandler.update(player, playersWithPack)
            }

            backpackHandler.update(player, playersWithPack)
        }
    }

    /**
     * Request models from players with resource pack active
     *
     * @param player the player
     */
    fun requestModelsFromActivePlayers(player: Player) {
        defaultScope.launch {
            playersWithPack.forEach {
                hatHandler.update(it, listOf(player))
                backpackHandler.update(it, listOf(player))
            }
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
            logger.severe("Failed to find a valid resource pack with UUID: $packId")
            logger.severe("Check your pack-id and make sure it is correct")

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
    private fun handleOnlinePlayers() {
        plugin.server.onlinePlayers.forEach {
            it.setResourcePack("http://cosmo.senseiju.me:8080/api/packs/${packId}?type=zip")
        }
    }

    /**
     * Register plugin events
     */
    private fun registerEvents() {
        plugin.registerEvents(PlayerListener(plugin))
    }

    /**
     * Register plugin commands
     */
    private fun registerCommands() {
        plugin.commandManager.register(CosmoCommand(plugin, this))
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