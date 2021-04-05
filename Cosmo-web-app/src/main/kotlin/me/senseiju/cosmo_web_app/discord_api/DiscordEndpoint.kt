package me.senseiju.cosmo_web_app.discord_api

private const val API_ENDPOINT = "https://discord.com/api"
private const val CDN_ENDPOINT = "https://cdn.discordapp.com"

enum class DiscordEndpoint(private val url: String, private val fromCdn: Boolean = false) {
    USERS("users"),
    TOKEN("oauth2/token"),
    AVATARS("avatars", true);

    override fun toString(): String {
        return "${if (fromCdn) CDN_ENDPOINT else API_ENDPOINT}/$url"
    }
}