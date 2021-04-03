package me.senseiju.cosmo_web_app.discord_api.requests

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_web_app.DISCORD_CLIENT_ID
import me.senseiju.cosmo_web_app.DISCORD_CLIENT_SECRET
import me.senseiju.cosmo_web_app.discord_api.DiscordEndpoint
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordAccessTokenResponse
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

/**
 * Exchanges an auth code for access token
 *
 * @param code the code
 * @return the token response
 */
fun exchangeCodeForAccessToken(code: String): DiscordAccessTokenResponse {
    val client = HttpClients.createDefault()
    val request = HttpPost("${DiscordEndpoint.TOKEN}")
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

fun revokeAccessToken(accessToken: String) {
    val client = HttpClients.createDefault()
    val request = HttpPost("${DiscordEndpoint.TOKEN}/revoke")
    val data = listOf(BasicNameValuePair("client_id", DISCORD_CLIENT_ID))
    request.entity = UrlEncodedFormEntity(data)
    request.setHeader("Content-Type", "application/x-www-form-urlencoded")

    client.execute(request)
}