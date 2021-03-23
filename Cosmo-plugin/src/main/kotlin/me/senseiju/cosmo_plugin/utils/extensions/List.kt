package me.senseiju.sennetmc.utils.extensions

fun List<String>.color(): List<String> {
    return this.map(String::color)
}