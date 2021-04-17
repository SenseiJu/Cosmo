package me.senseiju.cosmo_web_app

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.routes.*
import me.senseiju.cosmo_web_app.routes.api.api
import me.senseiju.cosmo_web_app.sessions.LoginSession

const val DISCORD_CLIENT_ID = "827167286104293407"
const val DISCORD_CLIENT_SECRET = "GuMXi4sH1uV_ilnDgW17r2eIrSl-xRG6"

const val PACK_PATH = "/cosmo/packs"

@kotlin.jvm.JvmOverloads
fun Application.cosmo(testing: Boolean = false) {

    install(ContentNegotiation) {
        json()
    }

    install(Sessions) {
        cookie<LoginSession>("LOGIN_SESSION")
    }

    routing {
        // BACKEND
        authenticate()
        logout()

        // PAGES
        assets()
        index()
        packs()
        modelGallery()

        // API
        api()
    }
}