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
                input(type = InputType.file, name = "model_textures") {
                    multiple = true
                }
                input(type = InputType.file, name = "model_item_json")
                input(type = InputType.file, name = "model_display_image")
                input(type = InputType.text, name = "name")
                input(type = InputType.radio, name = "model_type") {
                    value = "Hat"
                }
                input(type = InputType.radio, name = "model_type") {
                    value = "Backpack"
                }
                input(type = InputType.submit)
            }
        }
    }
}