package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import javax.sql.rowset.CachedRowSet
import kotlin.random.Random

class ResourcePackModelsPage(private val modelResults: CachedRowSet): Template<HTML> {
    override fun HTML.apply() {
        insert(HTMLHeadTemplate(), TemplatePlaceholder())

        body {
            id = "resource-pack-models-page"

            insert(ModelComponent(modelResults), TemplatePlaceholder())
        }
    }
}