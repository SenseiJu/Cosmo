package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.discord_api.requests.exchangeCodeForAccessToken
import me.senseiju.cosmo_web_app.sessions.LoginSession

private val DISCORD_OAUTH_ENDPOINT: String = if (System.getenv("COSMO_DEVELOPMENT_MODE").toBoolean()) {
    "https://discord.com/api/oauth2/authorize?client_id=827167286104293407&redirect_uri=http%3A%2F%2Fdev.cosmo.senseiju.me%3A8080%2Fauth&response_type=code&scope=identify"
} else {
    "https://discord.com/api/oauth2/authorize?client_id=827167286104293407&redirect_uri=https%3A%2F%2Fcosmo.senseiju.me%2Fauth&response_type=code&scope=identify"
}

fun Route.authenticate() {
    route("/auth") {
        handle {
            val code = try {
                call.parameters["code"] ?: throw java.lang.Exception()
            } catch (e: Exception) {
                call.respondRedirect(DISCORD_OAUTH_ENDPOINT)
                return@handle
            }

            val discordAccessTokenResponse = exchangeCodeForAccessToken(code)

            call.sessions.set(LoginSession(discordAccessTokenResponse.accessToken))

            call.respondRedirect("${AppPath.PACKS}")
        }
    }
}