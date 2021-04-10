package me.senseiju.cosmo_plugin.utils

enum class ColorScheme(private val rgb: String) {
    PRIMARY("c64fbd"),
    SECONDARY("3ab3da");

    override fun toString(): String {
        return "&#${this.rgb}"
    }
}