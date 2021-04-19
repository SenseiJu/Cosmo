package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.data_storage.wrappers.PackWrapper
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent
import java.util.*

class PacksPage(private val user: DiscordUserResponse, private val packs: Collection<PackWrapper>): Template<HTML> {
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            insert(HeaderComponent(user), TemplatePlaceholder())

            main {
                id = "pack-page"

                packs.forEach {
                    packComponent(it.name, it.packId)
                }

                newPackButton()
            }
        }
    }
}

private fun FlowContent.packComponent(packName: String, packId: UUID) {
    a(href = "/packs/$packId", classes = "pack-component") {
        div(classes = "content-wrapper") {
            contentHeader(packName, packId)

            p {
                + "Pack ID: $packId"
            }
        }
    }
}

private fun FlowContent.contentHeader(packName: String, packId: UUID) {
    div(classes = "content-header") {
        h1 {
            +packName
        }

        div(classes = "options") {
            i(classes = "gg-more-vertical-r")

            div(classes = "options-content") {
                removeButton(packId)
            }
        }
    }
}

private fun FlowContent.removeButton(packId: UUID) {
    button {
        onClick =
            """
            sendDeletePackRequest("$packId")
            """.trimIndent()

        + "Remove"
    }
}

private fun FlowContent.newPackButton() {
    button(classes = "new-pack") {
        onClick =
            """
            sendCreateNewPackRequest()
            """.trimIndent()

        + "+"
    }
}