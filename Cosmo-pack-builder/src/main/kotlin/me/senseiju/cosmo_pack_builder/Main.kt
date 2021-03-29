package me.senseiju.cosmo_pack_builder

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_pack_builder.json_templates.ItemJsonTemplate
import me.senseiju.cosmo_pack_builder.json_templates.createModelJson
import java.io.File
import java.util.*

const val PACK_TEMP_DIR = "/temp/"

fun main(args: Array<String>) {
    if (args.size < 3) {
        println("Invalid number of args, use `java -jar cosmo-pack-builder <models-dir> <<modelType> <modelData>... [modelType] [modelData]...>`")
        return
    }

    val models = File(args[0])

    var packId = stringToUUID(args.last())
    val modelTypeDataList = if (packId != null) {
        parseModelTypeDataMap(args.copyOfRange(1, args.size - 1))
    } else {
        packId = UUID.randomUUID()!!
        parseModelTypeDataMap(args.copyOfRange(1, args.size))
    }

    val pack = File("$PACK_TEMP_DIR/$packId/assets/minecraft/")
    modelTypeDataList.forEach { (modelType, modelDataList) ->
        val modelJson = createModelJson(modelType, modelDataList)
        val modelTypeLower = modelType.toString().toLowerCase()

        modelDataList.forEach {
            File(models, "$modelTypeLower/$it/textures").copyRecursively(File(pack, "/textures/$modelTypeLower/$it"))

            val itemFile = File(models, "/$modelTypeLower/$it/$it.json")
            val itemJson = Json.decodeFromString<ItemJsonTemplate>(itemFile.readText())
            itemJson.textures = reformatModelTextures(itemJson.textures ?: emptyMap(), modelType, it)

            File(pack, "/models/item/$modelTypeLower").mkdirs()
            File(pack, "/models/item/$modelTypeLower/$it.json").writeText(Json.encodeToString(itemJson))
        }

        File(pack, "/models/item/${modelType.material.toString().toLowerCase()}.json").writeText(Json.encodeToString(modelJson))

    }
    createMcmeta(packId)

    createZip(packId)
    createZipSHA1(packId)

    deleteDir(packId)
}

private fun parseModelTypeDataMap(args: Array<String>): Map<ModelType, Set<Int>> {
    val modelTypeDataList = hashMapOf<ModelType, HashSet<Int>>()
    var currentModelType: ModelType? = null

    args.forEach {
        val modelType = ModelType.parse(it)
        if (modelType != null) {
            currentModelType = modelType
            return@forEach
        }

        if (currentModelType == null) {
            println("No model type found: $it")
            return modelTypeDataList
        }
        modelTypeDataList.computeIfAbsent(currentModelType!!) { hashSetOf()}.add(it.toInt())
    }

    return modelTypeDataList
}

private fun reformatModelTextures(textures: Map<String, String>, modelType: ModelType, modelData: Int): Map<String, String> {
    return textures.map { (id, path) ->
        id to "${modelType.toString().toLowerCase()}/$modelData/${path.split("/").last()}"
    }.toMap()
}

private fun stringToUUID(string: String): UUID? {
    return try {
        UUID.fromString(string)
    } catch (e: Exception) {
        null
    }
}