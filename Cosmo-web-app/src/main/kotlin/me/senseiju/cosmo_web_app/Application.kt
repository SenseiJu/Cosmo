package me.senseiju.cosmo_web_app

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.sessions.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

const val DISCORD_CLIENT_ID = "827167286104293407"
const val DISCORD_CLIENT_SECRET = "GuMXi4sH1uV_ilnDgW17r2eIrSl-xRG6"

const val PACK_DIR = "D:\\Intellij Projects\\Cosmo\\Cosmo-web-app\\src\\test\\resources\\"

@kotlin.jvm.JvmOverloads
fun Application.cosmo(testing: Boolean = false) {

    install(ContentNegotiation) {
        json()
    }

    install(Sessions) {
        cookie<LoginSession>("LOGIN_SESSION")
    }

    routing {
        getResourcePack()
        loggedIn()
    }
}

fun Route.loggedIn() {
    route("/auth") {
        handle {
            call.respondRedirect("https://discord.com/api/oauth2/authorize?client_id=827167286104293407&redirect_uri=http%3A%2F%2Fcosmo.senseiju.me%3A8080%2Fhome&response_type=code&scope=identify")
        }
    }

    route("/home") {
        handle {
            val code = try {
                call.parameters["code"] ?: throw java.lang.Exception()
            } catch (e: Exception) {
                call.respondText("Bad code noob, stop trying to forge", status = HttpStatusCode.BadRequest)
                return@handle
            }

            val discordAccessTokenResponse = exchangeCodeForAccessToken(code)

            call.sessions.set(LoginSession(discordAccessTokenResponse.accessToken))

            call.respondText {
                "Logged in successfully with token, ${discordAccessTokenResponse.accessToken}"
            }
        }
    }
}

fun Route.getResourcePack() {
    route("/api") {
        get("{packId}") {
            val loginSession = call.sessions.get<LoginSession>()

            if (loginSession == null) {
                call.respondRedirect("http://cosmo.senseiju.me:8080/auth")
                return@get
            }

            println(getDiscordUser(loginSession.accessToken))

            val packId = try {
                UUID.fromString(call.parameters["packId"])
            } catch (e: Exception) {
                call.respondText("Pack with specified UUID does not exist", status = HttpStatusCode.BadRequest)
                return@get
            }

            val packZip = File("$PACK_DIR/$packId.zip")
            val packSha1 = File("$PACK_DIR/$packId.sha1")
            val packJson = File("$PACK_DIR/$packId.json")

            if (!(packZip.exists() && packSha1.exists() && packJson.exists())) {
                call.respondText("Pack with specified UUID does not exist", status = HttpStatusCode.BadRequest)
                return@get
            }

            when (call.request.queryParameters["type"]) {
                "zip" -> sendPackZip(call, packZip)
                "sha1" -> call.respondFile(packSha1)
                "json" -> call.respondFile(packJson)
                else -> {
                    call.respondText("Invalid file type supplied", status = HttpStatusCode.BadRequest)
                    return@get
                }
            }
        }
    }
}

private suspend fun sendPackZip(call: ApplicationCall, packZip: File) {
    call.response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(
            ContentDisposition.Parameters.FileName, "pack.zip"
        ).toString()
    )

    call.respondFile(packZip)
}

private fun exchangeCodeForAccessToken(code: String): DiscordAccessTokenResponse {
    val client = HttpClients.createDefault()
    val request = HttpPost("https://discord.com/api/oauth2/token")
    val data = listOf(
        BasicNameValuePair("client_id", DISCORD_CLIENT_ID),
        BasicNameValuePair("client_secret", DISCORD_CLIENT_SECRET),
        BasicNameValuePair("grant_type", "authorization_code"),
        BasicNameValuePair("code", code),
        BasicNameValuePair("redirect_uri", "http://cosmo.senseiju.me:8080/home"),
        BasicNameValuePair("scope", "identify")
    )
    request.entity = UrlEncodedFormEntity(data)
    request.setHeader("Content-Type", "application/x-www-form-urlencoded")

    return Json.decodeFromString(EntityUtils.toString(client.execute(request).entity))
}

private fun getDiscordUser(accessToken: String): String? {
    val client = HttpClients.createDefault()
    val request = HttpGet("https://discord.com/api/users/@me")
    request.setHeader("Authorization", "Bearer $accessToken")

    return EntityUtils.toString(client.execute(request).entity)
}