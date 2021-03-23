package me.senseiju.cosmo_plugin.utils.extensions

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

fun JavaPlugin.registerEvents(vararg listeners: Listener) {
    listeners.forEach { this.server.pluginManager.registerEvents(it, this) }
}