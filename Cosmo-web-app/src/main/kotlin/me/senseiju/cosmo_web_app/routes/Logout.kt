package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.discord_api.requests.revokeAccessToken
import me.senseiju.cosmo_web_app.sessions.LoginSession
import me.senseiju.cosmo_web_app.utils.getLoginSession

fun Route.logoutRoute() {
    route("/logout") {
        handle {
            val loginSession = getLoginSession(call, AppPath.AUTH) ?: return@handle

            revokeAccessToken(loginSession.accessToken)

            call.sessions.clear<LoginSession>()
            call.respondRedirect("${AppPath.INDEX}")
        }
    }
}