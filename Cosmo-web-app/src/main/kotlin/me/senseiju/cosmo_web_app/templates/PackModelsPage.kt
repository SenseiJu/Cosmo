package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent
import java.util.*

class PackModelsPage(
    private val user: DiscordUserResponse,
    private val packName: String,
    private val packId: UUID,
    private val models: Collection<ModelWrapper>,
    )
    : Template<HTML>
{
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            insert(HeaderComponent(user), TemplatePlaceholder())

            main {
                id = "resource-pack-models-page"

                h1 {
                    + packName
                }

                p {
                    + "Pack ID: $packId"

                    button {
                        onClick =
                            """
                            copyPackId("$packId");
                            """.trimIndent()

                        i(classes = "fas fa-copy")
                    }
                }

                insert(PackModelComponent(packId, models), TemplatePlaceholder())
            }
        }
    }
}