package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.data_storage.wrappers.PackWrapper
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent

class GalleryPage(
    private val user: DiscordUserResponse,
    private val packs: Collection<PackWrapper>,
    private val models: Collection<ModelWrapper>
) : Template<HTML> {
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            insert(HeaderComponent(user), TemplatePlaceholder())

            main {
                id = "resource-pack-models-page"

                h1 {
                    + "Gallery"
                }

                p {
                    + "You can find models to add to your packs from here. Click the '+' and choose which one to add it to."
                }

                insert(GalleryModelComponent(packs, models), TemplatePlaceholder())
            }
        }
    }
}