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

                h1 {
                    + "Your packs"
                }

                p {
                    + "Here you can find your packs. Click them to view the models or create a new one."
                }

                packs.forEach {
                    packComponent(it.name, it.packId)
                }

                newPackButton()
                newPackModal()
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
    button(classes = "new-pack-modal-button") {
        id = "new-pack-modal-button"

        + "+"
    }
}

private fun FlowContent.newPackModal() {
    div(classes = "new-pack-modal") {
        id = "new-pack-modal"

        form {
            onSubmit =
                """
                sendCreateNewPackRequest()
                """.trimIndent()

            input(type = InputType.text, name = "packName") {
                placeholder = "Pack name"
                id = "packName"
            }

            input(type = InputType.submit) {
                id = "submit"
                value = "Create"
            }
        }

        script(src = "/js/packs-modal.js") {}
    }
}