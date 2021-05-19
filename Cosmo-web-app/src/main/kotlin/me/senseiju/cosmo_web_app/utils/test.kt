package me.senseiju.cosmo_web_app.utils

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.sessions.LoginSession

suspend fun getLoginSession(call: ApplicationCall, redirect: AppPath? = null): LoginSession? {
    val loginSession = call.sessions.get<LoginSession>()

    if (loginSession == null) {
        if (redirect != null) {
            call.respondRedirect("$redirect")
        }
        return null
    }

    return loginSession
}

suspend fun getAuthenticatedDiscordUser(
    call: ApplicationCall,
    loginSession: LoginSession,
    redirect: AppPath? = null
): DiscordUserResponse? {
    val user = getDiscordUser(loginSession.accessToken)

    if (user.id == null) {
        if (redirect != null) {
            call.respondRedirect("$redirect")
        }
        return null
    }

    return user
}