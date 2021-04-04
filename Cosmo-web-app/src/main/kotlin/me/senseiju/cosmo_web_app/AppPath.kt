package me.senseiju.cosmo_web_app

enum class AppPath(private val url: String) {
    INDEX(""),
    HOME("home"),
    AUTH("auth"),
    PACKS("packs");

    override fun toString(): String {
        return "/$url"
    }
}