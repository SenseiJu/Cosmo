package me.senseiju.cosmo_plugin

import de.tr7zw.changeme.nbtapi.NBTItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.sennetmc.utils.extensions.color
import org.bukkit.inventory.ItemStack

@Serializable
data class Model(val modelData: Int, val name: String, val author: String, val modelType: ModelType) {

    @Transient
    val item: ItemStack = run {
        val nbtItem = NBTItem(
            ItemBuilder
                .from(modelType.material)
                .setName(name)
                .setLore("", "&7Author: $author".color())
                .build()
        )
        nbtItem.setInteger(CUSTOM_MODEL_DATA_TAG, modelData)
        nbtItem.item
    }

    fun applyItemToModel(original: ItemStack): ItemStack {
        val new = original.clone()
        new.type = modelType.material

        val nbtItem = NBTItem(new)
        nbtItem.setInteger(CUSTOM_MODEL_DATA_TAG, modelData)

        return nbtItem.item
    }
}
