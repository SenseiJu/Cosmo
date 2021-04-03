package me.senseiju.cosmo_web_app.discord_api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_web_app.DISCORD_CLIENT_ID
import me.senseiju.cosmo_web_app.DISCORD_CLIENT_SECRET
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

private const val BOT_TOKEN = "ODI3MTY3Mjg2MTA0MjkzNDA3.YGXFkQ.nokrzV4AfhlQddZoMEDFImaDlFk"
private const val API_ENDPOINT = "https://discord.com/api"
private const val TOKEN_ENDPOINT = "$API_ENDPOINT/oauth2/token"
private const val USERS_ENDPOINT = "$API_ENDPOINT/users"

fun exchangeCodeForAccessToken(code: String): DiscordAccessTokenResponse {
    val client = HttpClients.createDefault()
    val request = HttpPost(TOKEN_ENDPOINT)
    val data = listOf(
        BasicNameValuePair("client_id", DISCORD_CLIENT_ID),
        BasicNameValuePair("client_secret", DISCORD_CLIENT_SECRET),
        BasicNameValuePair("grant_type", "authorization_code"),
        BasicNameValuePair("code", code),
        BasicNameValuePair("redirect_uri", "http://cosmo.senseiju.me:8080/home"),
        BasicNameValuePair("scope", "identify email")
    )
    request.entity = UrlEncodedFormEntity(data)
    request.setHeader("Content-Type", "application/x-www-form-urlencoded")

    return Json.decodeFromString(EntityUtils.toString(client.execute(request).entity))
}

fun getDiscordUser(accessToken: String): DiscordUserResponse {
    val client = HttpClients.createDefault()
    val request = HttpGet("$USERS_ENDPOINT/@me")
    request.setHeader("Authorization", "Bearer $accessToken")

    return Json {ignoreUnknownKeys = true}.decodeFromString(EntityUtils.toString(client.execute(request).entity))
}

fun getDiscordUserById(userId: String): DiscordUserResponse {
    val client = HttpClients.createDefault()
    val request = HttpGet("$USERS_ENDPOINT/$userId")
    request.setHeader("Authorization", "Bot $BOT_TOKEN")

    return Json {ignoreUnknownKeys = true}.decodeFromString(EntityUtils.toString(client.execute(request).entity))
}