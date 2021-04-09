package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import java.util.*

class ModelComponent(private val packId: UUID, private val models: Collection<ModelWrapper>): Template<FlowContent> {
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

                    img(src = "https://www.arblease.co.uk/wp-content/uploads/2015/04/placeholder-200x200.png", alt = "temp")

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
    val modelData = model.modelData
    val modelType = model.modelType

    button {
        onClick = """
            sendDeleteModelFromPackRequest(
            "$packId",
            "$modelData", 
            "$modelType")
            """.trimIndent()

        + "Remove"
    }
}