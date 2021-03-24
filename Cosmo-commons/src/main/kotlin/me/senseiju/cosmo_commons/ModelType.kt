package me.senseiju.cosmo_commons

import org.bukkit.Material

enum class ModelType(val material: Material) {
    HAT(Material.BEDROCK),
    BACKPACK(Material.OBSIDIAN);

    fun getParentJsonElement(): String {
        return "minecraft:block/${this.material.toString().toLowerCase()}"
    }
}