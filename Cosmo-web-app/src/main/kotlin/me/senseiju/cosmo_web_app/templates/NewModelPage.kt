package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent

class NewModelPage(private val user: DiscordUserResponse) : Template<HTML> {
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            insert(HeaderComponent(user), TemplatePlaceholder())

            form("/models/new-model", method = FormMethod.post, encType = FormEncType.multipartFormData) {
                input(type = InputType.file, name = "images") {
                    multiple = true
                }
                input(type = InputType.text, name = "name")
                input(type = InputType.submit)
            }
        }
    }
}