package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.data_storage.wrappers.PackWrapper
import me.senseiju.cosmo_web_app.data_storage.wrappers.getImageURLForModel
import java.util.*
import javax.imageio.ImageIO

class GalleryModelComponent(
    private val packs: Collection<PackWrapper>,
    private val models: Collection<ModelWrapper>
    ) : Template<FlowContent>
{
    override fun FlowContent.apply() {
        ul(classes = "model") {
            models.forEach { model ->
                li {
                    div(classes = "top") {
                        h1 {
                            + "${model.name}"
                        }

                        options(packs, model)
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

private fun FlowContent.options(packs: Collection<PackWrapper>, model: ModelWrapper) {
    div(classes = "options") {
        i(classes = "fas fa-plus") {
            style = "font-size: 1.3em;"
        }

        div(classes = "options-content") {
            packs.forEach {
                subscribeButton(it, model)
            }
        }
    }
}

private fun FlowContent.subscribeButton(pack: PackWrapper, model: ModelWrapper) {
    button {
        onClick = """
            sendSubscribeModelToPackRequest(
            "${pack.packId}",
            "${model.modelData}", 
            "${model.modelType}")
            """.trimIndent()

        + pack.name
    }
}