package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import me.senseiju.cosmo_web_app.discord_api.responses.DiscordUserResponse
import me.senseiju.cosmo_web_app.templates.common_components.HTMLHeadComponent
import me.senseiju.cosmo_web_app.templates.common_components.HeaderComponent
import javax.sql.rowset.CachedRowSet

class PackModelsPage(
    private val user: DiscordUserResponse,
    private val modelResults: CachedRowSet
    )
    : Template<HTML>
{
    override fun HTML.apply() {
        insert(HTMLHeadComponent(), TemplatePlaceholder())

        body {
            id = "resource-pack-models-page"

            insert(HeaderComponent(user), TemplatePlaceholder())

            insert(ModelComponent(modelResults), TemplatePlaceholder())
        }
    }
}