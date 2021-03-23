package me.senseiju.cosmo_pack_builder

import me.senseiju.cosmo_commons.ModelType

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Invalid number of args, use `java -jar cosmo-pack-builder <modelType> <modelData>...`")
        return
    }

    val modelType = ModelType.valueOf(args[0].toUpperCase())
}