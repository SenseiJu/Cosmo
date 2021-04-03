package me.senseiju.cosmo_web_app.templates

import io.ktor.html.*
import kotlinx.html.*
import javax.sql.rowset.CachedRowSet
import kotlin.random.Random

class ModelPageTemplate(private val accessToken: String, private val modelResults: CachedRowSet): Template<HTML> {
    override fun HTML.apply() {
        head {
            script(src = "js/scripts.js") {}
            link(rel = "stylesheet", href = "css/styles.css?v=${Random.nextInt()}", type = "text/css")
        }

        body {
            insert(ModelComponent(modelResults), TemplatePlaceholder())
        }
    }
}