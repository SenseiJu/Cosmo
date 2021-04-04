package me.senseiju.cosmo_web_app.routes

import io.ktor.http.content.*
import io.ktor.routing.*

fun Route.assets() {
    static("css") {
        resources("css")
    }

    static("js") {
        resources("js")
    }

    static("img") {
        resources("img")
    }
}