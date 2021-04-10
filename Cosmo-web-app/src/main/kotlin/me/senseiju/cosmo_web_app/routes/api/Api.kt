package me.senseiju.cosmo_web_app.routes.api

import io.ktor.routing.*

fun Route.api() {
    route("/api") {
        apiPacks()
        apiModels()
    }
}