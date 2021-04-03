package me.senseiju.cosmo_web_app.discord_api.requests

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_web_app.discord_api.DiscordEndpoint
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

private const val BOT_TOKEN = "ODI3MTY3Mjg2MTA0MjkzNDA3.YGXFkQ.nokrzV4AfhlQddZoMEDFImaDlFk"

/**
 * Gets a user by access token
 *
 * @param accessToken the access token
 * @return the user response
 */
fun getDiscordUser(accessToken: String): DiscordUserResponse {
    val client = HttpClients.createDefault()
    val request = HttpGet("${DiscordEndpoint.USERS}/@me")
    request.setHeader("Authorization", "Bearer $accessToken")

    return Json {ignoreUnknownKeys = true}.decodeFromString(EntityUtils.toString(client.execute(request).entity))
}

/**
 * Gets a user by user id
 *
 * Note that this take approximately 300-500ms to complete
 *
 * @param userId the user id
 * @return the user response
 */
fun getDiscordUserById(userId: String): DiscordUserResponse {
    val client = HttpClients.createDefault()
    val request = HttpGet("${DiscordEndpoint.USERS}/$userId")
    request.setHeader("Authorization", "Bot $BOT_TOKEN")

    return Json {ignoreUnknownKeys = true}.decodeFromString(EntityUtils.toString(client.execute(request).entity))
}