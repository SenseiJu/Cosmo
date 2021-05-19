package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.routing.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.templates.DownloadPage
import me.senseiju.cosmo_web_app.utils.getLoginSession

fun Route.downloadRoute() {
    route("/download") {
        handle {
            val loginSession = getLoginSession(call, AppPath.AUTH) ?: return@handle

            val user = getDiscordUser(loginSession.accessToken)
            if (user.id == null) {
                call.respondRedirect("${AppPath.AUTH}")
                return@handle
            }

            call.respondHtmlTemplate(DownloadPage(user)) {}
        }
    }
}