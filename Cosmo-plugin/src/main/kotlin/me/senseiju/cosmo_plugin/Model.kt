package me.senseiju.cosmo_plugin

import de.tr7zw.changeme.nbtapi.NBTItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_plugin.utils.ColorScheme
import me.senseiju.cosmo_plugin.utils.extensions.color
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

@Serializable
data class Model(val modelData: Int, val name: String, val author: String, val modelType: ModelType) {

    @Transient
    val item: ItemStack = run {
        val nbtItem = NBTItem(
            ItemBuilder
                .from(modelType.material)
                .setName("${ColorScheme.SECONDARY}$name".color())
                .setLore("", "&7Author: $author".color())
                .build()
        )
        nbtItem.setInteger(CUSTOM_MODEL_DATA_TAG, modelData)
        nbtItem.item
    }

    fun applyItemToModel(original: ItemStack): ItemStack {
        val new = original.clone()
        new.type = modelType.material

        if (original.itemMeta is Damageable) {
            val damageable = original.itemMeta as Damageable
            val maxDurability = original.type.maxDurability
            val durabilityComponent = Component.text("Durability: ${maxDurability - damageable.damage} / $maxDurability")
                .color(TextColor.color(255, 255, 255))
                .decoration(TextDecoration.ITALIC, false)

            with(listOf(Component.empty(), durabilityComponent)) {
                new.lore()?.addAll(this) ?: new.lore(this)
            }
        }

        val nbtItem = NBTItem(new)
        nbtItem.setInteger(CUSTOM_MODEL_DATA_TAG, modelData)

        return nbtItem.item
    }
}
