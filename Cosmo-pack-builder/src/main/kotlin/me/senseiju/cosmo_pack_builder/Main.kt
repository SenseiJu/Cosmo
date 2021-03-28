package me.senseiju.cosmo_pack_builder

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_pack_builder.json_templates.ItemJsonTemplate
import me.senseiju.cosmo_pack_builder.json_templates.createModelJson
import me.senseiju.cosmo_pack_builder.json_templates.createPackMcmetaJson
import net.lingala.zip4j.ZipFile
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.util.*

const val PACK_TEMP_DIR = "/temp/"
const val PACK_BASE_DIR = "/assets/minecraft/"

private val models = File("D:\\Intellij Projects\\Cosmo\\Cosmo-pack-builder\\src\\test\\resources\\models")

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Invalid number of args, use `java -jar cosmo-pack-builder <models-dir> <<modelType> <modelData>... [modelType] [modelData]...>`")
        return
    }

    var packId = stringToUUID(args.last())
    val modelTypeDataList = if (packId != null) {
        parseModelTypeDataMap(args.copyOfRange(0, args.lastIndex - 1))
    } else {
        packId = UUID.randomUUID()
        parseModelTypeDataMap(args)
    }

    val pack = File("/temp/$packId/$PACK_BASE_DIR")
    modelTypeDataList.forEach { (modelType, modelDataList) ->
        val modelJson = createModelJson(modelType, modelDataList)
        val modelTypeLower = modelType.toString().toLowerCase()

        modelDataList.forEach {
            File(models, "$modelTypeLower/$it/textures").copyRecursively(File(pack, "/textures/$modelTypeLower/$it"))

            val itemFile = File(models, "/$modelTypeLower/$it/$it.json")
            val itemJson = Json.decodeFromString<ItemJsonTemplate>(itemFile.readText())
            itemJson.textures = reformatModelTextures(itemJson.textures ?: emptyMap(), modelType, it)

            File(pack, "/models/item/$modelTypeLower").mkdirs()
            val a = File(pack, "/models/item/$modelTypeLower/$it.json")
            a.writeText(Json.encodeToString(itemJson))
        }

        File(pack, "/models/item/${modelType.material.toString().toLowerCase()}.json").writeText(Json.encodeToString(modelJson))

    }
    val mcmetaFile = File("/temp/$packId/pack.mcmeta")
    mcmetaFile.writeText(Json.encodeToString(createPackMcmetaJson()))

    val zipFile = ZipFile(File("/temp/$packId.zip"))
    zipFile.addFolder(File("/temp/$packId/assets"))
    zipFile.addFile(mcmetaFile)


    File("/temp/$packId.sha1").writeText(DigestUtils.sha1Hex(File("/temp/$packId.zip").inputStream()))
    File("/temp/$packId").deleteRecursively()
}

private fun saveToZip() {

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