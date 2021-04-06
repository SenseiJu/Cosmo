package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUserById
import java.util.*
import javax.sql.rowset.CachedRowSet

class ModelComponent(private val packId: UUID, private val modelResults: CachedRowSet): Template<FlowContent> {
    override fun FlowContent.apply() {
        ul(classes = "model") {
            while (modelResults.next()) {
                li {
                    div(classes = "top") {
                        h1 {
                            + modelResults.getString("name")
                        }

                        options(packId, modelResults)
                    }

                    p {
                        + "Author: ${getDiscordUserById(modelResults.getString("user_id")).username}"
                    }
                }
            }
        }
    }
}

private fun FlowContent.options(packId: UUID, modelResults: CachedRowSet) {
    div(classes = "options") {
        i(classes = "gg-more-vertical-r")

        div(classes = "options-content") {
            removeButton(packId, modelResults)
        }
    }
}

private fun FlowContent.removeButton(packId: UUID, modelResults: CachedRowSet) {
    val modelData = modelResults.getString("model_data")
    val modelType = modelResults.getString("model_type")

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