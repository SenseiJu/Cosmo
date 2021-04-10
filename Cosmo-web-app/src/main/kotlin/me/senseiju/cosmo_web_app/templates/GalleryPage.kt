package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.id
import kotlinx.html.main
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent
import java.util.*

class GalleryPage(private val user: DiscordUserResponse, private val models: Collection<ModelWrapper>) : Template<HTML> {
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            insert(HeaderComponent(user), TemplatePlaceholder())

            main {
                id = "resource-pack-models-page"

                insert(PackModelComponent(UUID.randomUUID(), models), TemplatePlaceholder())
            }
        }
    }
}