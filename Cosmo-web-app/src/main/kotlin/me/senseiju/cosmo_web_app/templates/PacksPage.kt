package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.HTML
import kotlinx.html.body
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent

class PacksPage(private val user: DiscordUserResponse): Template<HTML> {
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            insert(HeaderComponent(user), TemplatePlaceholder())
        }
    }
}