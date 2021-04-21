package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.sessions.LoginSession
import me.senseiju.cosmo_web_app.templates.PluginPage

fun Route.download() {
    route("/download") {
        handle {
            val loginSession = call.sessions.get<LoginSession>()
            if (loginSession == null) {
                call.respondRedirect("${AppPath.AUTH}")
                return@handle
            }

            val user = getDiscordUser(loginSession.accessToken)
            if (user.id == null) {
                call.respondRedirect("${AppPath.AUTH}")
                return@handle
            }

            call.respondHtmlTemplate(PluginPage(user)) {}
        }
    }
}