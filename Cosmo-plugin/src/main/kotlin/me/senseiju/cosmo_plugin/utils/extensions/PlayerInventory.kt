package me.senseiju.cosmo_plugin.utils.extensions

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

fun PlayerInventory.addItemOrDropNaturally(location: Location, item: ItemStack) {
    val remaining = this.addItem(item)
    if (remaining.isNotEmpty()) {
        remaining.values.forEach { location.world.dropItemNaturally(location, it) }
    }
}