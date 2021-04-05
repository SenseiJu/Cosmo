package me.senseiju.cosmo_web_app.templates.common_components

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUserAvatar
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse

class HeaderComponent(private val user: DiscordUserResponse): Template<FlowContent> {
    override fun FlowContent.apply() {
        header {
            h1(classes = "title") {
                + "Cosmo"
            }

            pageOptions()

            userOptions(user)
        }
    }
}

private fun FlowContent.pageOptions() {
    div(classes = "page-options") {
        a(href = "/gallery") {
            + "Gallery"
        }

        a(href = "/plugin") {
            + "Plugin"
        }
    }
}

private fun FlowContent.userOptions(user: DiscordUserResponse) {
    div(classes = "user-options") {
        div(classes = "user-options-button") {
            span(classes = "dropdown-arrow")

            img(src = getDiscordUserAvatar(user), alt = "discord-avatar")
        }

        div(classes = "user-options-content") {
            a(href = "/packs") {
                + "Logged in as ${user.username}"
            }

            div(classes = "spacer")

            a(href = "/packs") {
                + "Your packs"
            }

            a(href = "/models") {
                + "Your models"
            }

            div(classes = "spacer")

            a(href = "/logout") {
                + "Logout"
            }
        }
    }
}