package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.data_storage.wrappers.PackWrapper
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

                    img(src = "https://www.arblease.co.uk/wp-content/uploads/2015/04/placeholder-200x200.png", alt = "temp")

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
        i(classes = "gg-more-vertical-r")

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