package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import kotlin.random.Random

class HTMLHeadTemplate: Template<HTML> {
    override fun HTML.apply() {
        head {
            title {
                + "Cosmo"
            }

            script(src = "/js/scripts.js") {}
            link(rel = "stylesheet", href = "/css/styles.css?v=${Random.nextInt()}", type = "text/css")
        }
    }
}