package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.discord_api.getDiscordUserById
import javax.sql.rowset.CachedRowSet

class ModelComponent(private val modelResults: CachedRowSet): Template<FlowContent> {
    override fun FlowContent.apply() {
        ul(classes = "model") {
            while (modelResults.next()) {
                li {
                    div(classes = "top") {
                        h1 {
                            +modelResults.getString("name")
                        }

                        options(modelResults)
                    }

                    p {
                        + "Author: ${getDiscordUserById(modelResults.getString("user_id")).username}"
                    }
                }
            }
        }
    }
}

@HtmlTagMarker
private fun FlowContent.options(modelResults: CachedRowSet) {
    div(classes = "options") {
        i(classes = "gg-more-vertical-r")

        div(classes = "options-content") {
            removeButton(modelResults)
        }
    }
}

@HtmlTagMarker
private fun FlowContent.removeButton(modelResults: CachedRowSet) {
    button {
        onClick = """
            sendDeleteModelFromPackRequest(
            '${modelResults.getString("model_data")}', 
            '${modelResults.getString("model_type")}')
            """.trimIndent()

        attributes["model_data"] = modelResults.getString("model_data")
        attributes["model_type"] = modelResults.getString("model_type")

        + "Remove"
    }
}