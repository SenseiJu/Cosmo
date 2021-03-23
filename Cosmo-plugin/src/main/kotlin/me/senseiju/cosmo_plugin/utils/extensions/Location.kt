package me.senseiju.sennetmc.utils.extensions

import org.bukkit.Bukkit
import org.bukkit.Location

fun Location.serialize(): String {
    return "${world.name}:$x:$y:$z"
}

fun Location.serializeFull(): String {
    return "${world.name}:$x:$y:$z:$yaw:$pitch"
}

fun deserializeLocation(s: String): Location {
    val location = s.split(":")
    return Location(
        Bukkit.getWorld(location[0]),
        location[1].toDouble(),
        location[2].toDouble(),
        location[3].toDouble()
    )
}

fun deserializeFullLocation(s: String): Location {
    val location = s.split(":")
    return Location(
        Bukkit.getWorld(location[0]),
        location[1].toDouble(),
        location[2].toDouble(),
        location[3].toDouble(),
        location[4].toFloat(),
        location[5].toFloat()
    )
}