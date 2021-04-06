package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent

class IndexPage: Template<HTML> {
    override fun HTML.apply() {
        id = "index"

        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body(classes = "no-transition-on-load") {
            stars()

            div {
                id = "title"

                h1 {
                    + "Cosmo"
                }

                br()

                + "Platform for custom Minecraft cosmetics"

                br()

                discordButton()
            }
        }
    }
}

private fun FlowContent.stars() {
    for (i in 1..3) {
        div {
            id = "stars$i"
        }
    }
}

private fun FlowContent.discordButton() {
    a {
        href = "/auth"

        div {
            img("discord-logo", "/img/discord.png")
            p {
                + "Login with discord"
            }
        }
    }
}