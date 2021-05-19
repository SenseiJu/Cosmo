package me.senseiju.cosmo_web_app.templates.common_components

import io.ktor.html.*
import kotlinx.html.*
import kotlin.random.Random

class HTMLHeadComponent: Template<HTML> {
    override fun HTML.apply() {
        head {
            title {
                + "Cosmo"
            }

            script(src = "/js/scripts.js") {}
            script(src = "https://kit.fontawesome.com/7ef4e3c4f0.js") {}

            link(rel = "stylesheet", href = "/css/styles.css?v=${Random.nextInt()}", type = "text/css")
            link(rel = "icon", href = "/img/logo.jpg")
        }
    }
}