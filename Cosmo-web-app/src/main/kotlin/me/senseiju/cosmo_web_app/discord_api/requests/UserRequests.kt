package me.senseiju.cosmo_web_app.discord_api.requests

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_web_app.CachedHashMap
import me.senseiju.cosmo_web_app.discord_api.DiscordEndpoint
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.util.concurrent.TimeUnit

private const val BOT_TOKEN = "ODI3MTY3Mjg2MTA0MjkzNDA3.YGXFkQ.nokrzV4AfhlQddZoMEDFImaDlFk"

private val cachedDiscordUsers = CachedHashMap<String, DiscordUserResponse>(
    TimeUnit.HOURS.toMillis(1),
    TimeUnit.MINUTES.toMillis(30)
)

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
    if (cachedDiscordUsers.contains(userId)) {
        return cachedDiscordUsers.get(userId)!!
    }

    val client = HttpClients.createDefault()
    val request = HttpGet("${DiscordEndpoint.USERS}/$userId")
    request.setHeader("Authorization", "Bot $BOT_TOKEN")

    val user = Json {
        ignoreUnknownKeys = true
    }.decodeFromString<DiscordUserResponse>(EntityUtils.toString(client.execute(request).entity))

    cachedDiscordUsers.set(userId, user)

    return user
}

/**
 * Gets a user avatar
 *
 * @param user the user
 * @return the image URL
 */
fun getDiscordUserAvatar(user: DiscordUserResponse): String {
    return "${DiscordEndpoint.AVATARS}/${user.id}/${user.avatar}.png"
}