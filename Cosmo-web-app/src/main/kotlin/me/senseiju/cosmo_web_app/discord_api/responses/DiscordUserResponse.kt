package me.senseiju.cosmo_web_app.discord_api.responses

import kotlinx.serialization.Serializable

@Serializable
data class DiscordUserResponse (
    val id: String? = null,
    val username: String? = null
)