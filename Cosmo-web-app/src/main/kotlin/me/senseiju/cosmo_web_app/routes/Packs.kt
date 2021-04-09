package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.data_storage.isUserPackOwner
import me.senseiju.cosmo_web_app.data_storage.selectModelsFromPackJoinedWithModels
import me.senseiju.cosmo_web_app.data_storage.selectPackName
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.sessions.LoginSession
import me.senseiju.cosmo_web_app.templates.PackModelsPage
import me.senseiju.cosmo_web_app.templates.PacksPage
import java.util.*

fun Route.packs() {
    route("/packs") {
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

            call.respondHtmlTemplate(PacksPage(user)) {}
        }

        get("{packId}") {
            val loginSession = call.sessions.get<LoginSession>()
            if (loginSession == null) {
                call.respondRedirect("${AppPath.AUTH}")
                return@get
            }

            val user = getDiscordUser(loginSession.accessToken)
            if (user.id == null) {
                call.respondRedirect("${AppPath.AUTH}")
                return@get
            }

            val packId = try {
                UUID.fromString(call.parameters["packId"])
            } catch (e: Exception) {
                call.respondRedirect("${AppPath.PACKS}")
                return@get
            }

            if (!isUserPackOwner(packId, user.id)) {
                call.respondRedirect("${AppPath.PACKS}")
                return@get
            }

            val models = selectModelsFromPackJoinedWithModels(packId)

            call.respondHtmlTemplate(PackModelsPage(user, selectPackName(packId), packId, models)) {}
        }
    }
}