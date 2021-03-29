package me.senseiju.sennetmc.utils.extensions

import me.senseiju.cosmo_plugin.utils.extensions.color

fun List<String>.color(): List<String> {
    return this.map(String::color)
}