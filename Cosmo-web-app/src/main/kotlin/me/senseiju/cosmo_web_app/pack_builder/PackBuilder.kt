package me.senseiju.cosmo_web_app.pack_builder

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.PACK_PATH
import me.senseiju.cosmo_web_app.data_storage.selectModelsFromPackJoinedWithModels
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.pack_builder.json_templates.ItemJsonTemplate
import me.senseiju.cosmo_web_app.pack_builder.json_templates.createModelJson
import me.senseiju.cosmo_web_app.pack_builder.json_templates.createPackMcmetaJson
import net.lingala.zip4j.ZipFile
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.util.*

/**
 * Pack builder is used to create the zip, json and sha files for delivering packs
 *
 * @param modelsPath the path to where models are stored
 * @param tempPath the path to build the resource pack
 * @param packsPath the path to output the final files
 */
private class PackBuilder(
    modelsPath: String = "/cosmo/models",
    tempPath: String = "/cosmo/temp",
    packsPath: String = "/cosmo/packs"
) {
    private val modelsDir = File(modelsPath)
    private val tempDir = File(tempPath)
    private val packsDir = File(packsPath)

    fun build(packId: UUID, vararg models: ModelWrapper) {
        val modelsMap = hashMapOf<ModelType, ArrayList<Int>>()
        models.forEach {
            modelsMap.computeIfAbsent(it.modelType) { arrayListOf() }.add(it.modelData)
        }

        deleteAllPackFiles(packId)

        buildPackZipAndSHA(packId, modelsMap)
        buildJson(packId, *models)

        deleteTempPackFiles(packId)
    }

    /**
     * Deletes all files related to the pack id
     *
     * @param packId the pack id
     */
    fun deleteAllPackFiles(packId: UUID) {
        deleteTempPackFiles(packId)

        File(packsDir, "$packId.zip").delete()
        File(packsDir, "$packId.sha1").delete()
        File(packsDir, "$packId.json").delete()
    }

    /**
     * Delete temp files related to the pack id
     *
     * @param packId the pack id
     */
    private fun deleteTempPackFiles(packId: UUID) {
        File(tempDir, "$packId").deleteRecursively()
    }

    /**
     * Build a pack's zip file and SHA-1 hash
     *
     * @param packId the pack id
     * @param models the models to build inside the pack
     */
    private fun buildPackZipAndSHA(packId: UUID, models: Map<ModelType, Collection<Int>>) {
        val pack = File(tempDir, "$packId/assets/minecraft/")
        models.forEach { (modelType, modelDataList) ->
            val modelJson = createModelJson(modelType, modelDataList)
            val modelTypeLower = modelType.toString().toLowerCase()

            modelDataList.forEach {
                File(modelsDir, "$modelTypeLower/$it/textures").copyRecursively(
                    File(pack, "/textures/$modelTypeLower/$it")
                )

                val itemFile = File(modelsDir, "/$modelTypeLower/$it/$it.json")
                val itemJson = Json.decodeFromString<ItemJsonTemplate>(itemFile.readText())
                itemJson.textures = reformatModelTextures(itemJson.textures ?: emptyMap(), modelType, it)

                File(pack, "/models/item/$modelTypeLower").mkdirs()
                File(pack, "/models/item/$modelTypeLower/$it.json").writeText(Json.encodeToString(itemJson))
            }

            File(pack, "/models/item/${modelType.material.toString().toLowerCase()}.json")
                .writeText(Json.encodeToString(modelJson))

        }
        createMcmeta(packId)

        createZip(packId)
        createZipSHA1(packId)
    }

    /**
     * Build the json file for a pack, used by the plugin to map models from the pack
     *
     * @param packId the pack id
     * @param models the models
     */
    private fun buildJson(packId: UUID, vararg models: ModelWrapper) {
        File(packsDir, "$packId.json").writeText(Json.encodeToString(models))
    }

    /**
     * Create the mcmeta file used to identify a pack
     *
     * @param packId the pack id
     */
    private fun createMcmeta(packId: UUID) {
        File(tempDir, "$packId/pack.mcmeta").writeText(Json.encodeToString(createPackMcmetaJson()))
    }

    /**
     * Create the zip file
     *
     * @param packId the pack id
     */
    private fun createZip(packId: UUID) {
        val zipFile = ZipFile(File(packsDir, "$packId.zip"))
        zipFile.addFolder(File(tempDir, "$packId/assets"))
        zipFile.addFile(File(tempDir, "$packId/pack.mcmeta"))
    }

    /**
     * Create SHA-1 file for the zip
     *
     * @param packId the pack id
     */
    private fun createZipSHA1(packId: UUID) {
        File(packsDir, "$packId.sha1").writeText(digestFileToSHA1(packId))
    }

    /**
     * Generate the SHA-1 hash for the zip
     *
     * @param packId the pack id
     *
     * @return the hash as a hex string
     */
    private fun digestFileToSHA1(packId: UUID): String {
        return File(packsDir, "$packId.zip").inputStream().use {
            DigestUtils.sha1Hex(it)
        }
    }

    private fun reformatModelTextures(
        textures: Map<String, String>,
        modelType: ModelType,
        modelData: Int
    ): Map<String, String> {
        return textures.map { (id, path) ->
            id to "${modelType.toString().toLowerCase()}/$modelData/${path.split("/").last()}"
        }.toMap()
    }
}

private val packBuilder = PackBuilder(packsPath = PACK_PATH)

suspend fun buildPack(packId: UUID) {
    val models = selectModelsFromPackJoinedWithModels(packId).toTypedArray()
    if (models.isEmpty()) {
        deletePackFiles(packId)
        return
    }
    packBuilder.build(packId, *models)
}

fun deletePackFiles(packId: UUID) {
    packBuilder.deleteAllPackFiles(packId)
}