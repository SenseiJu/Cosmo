package me.senseiju.cosmo_commons

import org.bukkit.Material

enum class ModelType(val material: Material) {
    HAT(Material.BEDROCK),
    BACKPACK(Material.OBSIDIAN);

    companion object {
        fun parse(string: String): ModelType? {
            if (values().map { it.toString() }.contains(string.uppercase())) {
                return valueOf(string.uppercase())
            }

            return null
        }
    }

    fun getParentJsonElement(): String {
        return "minecraft:block/${this.material.toString().lowercase()}"
    }
}