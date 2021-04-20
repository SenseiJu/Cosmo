package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.data_storage.wrappers.getImageURLForModel
import java.util.*

class PackModelComponent(private val packId: UUID, private val models: Collection<ModelWrapper>): Template<FlowContent> {
    override fun FlowContent.apply() {
        ul(classes = "model") {
            models.forEach { model ->
                li {
                    div(classes = "top") {
                        h1 {
                            + "${model.name}"
                        }

                        options(packId, model)
                    }

                    img(src = getImageURLForModel(model), alt = "temp")

                    p {
                        + "Author: ${model.author}"
                    }
                }
            }
        }
    }
}

private fun FlowContent.options(packId: UUID, model: ModelWrapper) {
    div(classes = "options") {
        i(classes = "gg-more-vertical-r")

        div(classes = "options-content") {
            removeButton(packId, model)
        }
    }
}

private fun FlowContent.removeButton(packId: UUID, model: ModelWrapper) {
    button {
        onClick =
            """
            sendDeleteModelFromPackRequest(
            "$packId",
            "${model.modelData}", 
            "${model.modelType}")
            """.trimIndent()

        + "Remove"
    }
}