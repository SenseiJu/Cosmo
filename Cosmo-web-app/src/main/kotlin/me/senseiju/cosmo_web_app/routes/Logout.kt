package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.discord_api.requests.revokeAccessToken
import me.senseiju.cosmo_web_app.sessions.LoginSession

fun Route.logout() {
    route("/logout") {
        handle {
            val loginSession = call.sessions.get<LoginSession>()
            if (loginSession == null) {
                call.respondRedirect("${AppPath.INDEX}")
                return@handle
            }

            revokeAccessToken(loginSession.accessToken)

            call.sessions.clear<LoginSession>()
            call.respondRedirect("${AppPath.INDEX}")
        }
    }
}