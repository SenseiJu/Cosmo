package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.sessions.LoginSession
import me.senseiju.cosmo_web_app.templates.IndexPage

fun Route.index() {
    handle {
        val loginSession = call.sessions.get<LoginSession>()
        if (loginSession != null) {
            call.respondRedirect("${AppPath.PACKS}")
            return@handle
        }

        call.respondHtmlTemplate(IndexPage()) {}
    }
}