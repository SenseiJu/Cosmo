package me.senseiju.cosmo_pack_builder

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_pack_builder.json_templates.*

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Invalid number of args, use `java -jar cosmo-pack-builder <modelType> <modelData>...`")
        return
    }

    val modelType = ModelType.valueOf(args[0].toUpperCase())
    val modelDataList = args.copyOfRange(1, args.size).map { it.toInt() }

    val modelJson = createModelJson(modelType, modelDataList)

    val packJson =

    println(Json.encodeToString(modelJson))
}