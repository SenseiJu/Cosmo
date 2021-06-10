package me.senseiju.cosmo_plugin.utils.extensions

import org.bukkit.scheduler.BukkitRunnable

fun newRunnable(block: () -> Unit): BukkitRunnable {
    return object : BukkitRunnable() {
        override fun run() {
            block.invoke()
        }
    }
}