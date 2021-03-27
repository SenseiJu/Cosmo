package me.senseiju.cosmo_pack_builder

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_pack_builder.json_templates.ItemJsonTemplate
import me.senseiju.cosmo_pack_builder.json_templates.createModelJson
import java.io.File
import java.util.*

const val packBaseDir = "/assets/minecraft"
const val packItemDir = "$packBaseDir/models/item"
const val packTexturesDir = "$packBaseDir/textures"

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Invalid number of args, use `java -jar cosmo-pack-builder <<modelType> <modelData>... [modelType] [modelData]...>`")
        return
    }

    var packId = stringToUUID(args.last())
    val modelTypeDataList = if (packId != null) {
        parseModelTypeDataMap(args.copyOfRange(0, args.lastIndex - 1))
    } else {
        packId = UUID.randomUUID()
        parseModelTypeDataMap(args)
    }

    modelTypeDataList.forEach { (modelType, modelDataList) ->
        val modelJson = createModelJson(modelType, modelDataList)

        val pack = File("/temp/$packId/assets/minecraft")
        val models = File("D:\\Intellij Projects\\Cosmo\\Cosmo-pack-builder\\src\\test\\resources\\models")

        modelDataList.forEach {
            File(models, "$modelType/$it/textures").copyRecursively(File(pack, "/textures/$modelType/$it"))

            val itemFile = File(models, "/$modelType/$it/$it.json")
            val itemJson = Json.decodeFromString<ItemJsonTemplate>(itemFile.readText())
            itemJson.textures = reformatModelTextures(itemJson.textures ?: emptyMap(), modelType, it)

            File(pack, "/models/item/$modelType").mkdirs()
            File(pack, "/models/item/$modelType/$it.json").writeText(Json.encodeToString(itemJson))
        }

        File(pack, "/models/item/$modelType.json").writeText(Json.encodeToString(modelJson))
    }
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
        id to "$modelType/$modelData/${path.split("/").last()}"
    }.toMap()
}

private fun stringToUUID(string: String): UUID? {
    return try {
        UUID.fromString(string)
    } catch (e: Exception) {
        null
    }
}