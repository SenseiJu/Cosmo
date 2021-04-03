package me.senseiju.cosmo_web_app.discord_api

private const val API_ENDPOINT = "https://discord.com/api"

enum class DiscordEndpoint(private val url: String) {
    USERS("users"),
    TOKEN("token");

    override fun toString(): String {
        return "$API_ENDPOINT/$url"
    }
}