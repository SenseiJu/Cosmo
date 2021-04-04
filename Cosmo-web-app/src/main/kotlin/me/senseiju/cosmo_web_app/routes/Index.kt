package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.routing.*
import me.senseiju.cosmo_web_app.templates.IndexPage

fun Route.index() {
    handle {
        call.respondHtmlTemplate(IndexPage()) {}
    }
}