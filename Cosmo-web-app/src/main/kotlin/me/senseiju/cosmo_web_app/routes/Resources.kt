package me.senseiju.cosmo_web_app.routes

import io.ktor.http.content.*
import io.ktor.routing.*
import me.senseiju.cosmo_web_app.MODELS_PATH
import java.io.File

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

    static("models") {
        files(File(MODELS_PATH))
    }
}