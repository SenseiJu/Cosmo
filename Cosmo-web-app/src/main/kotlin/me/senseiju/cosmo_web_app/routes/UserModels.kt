package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.TEMP_PATH
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.sessions.LoginSession
import me.senseiju.cosmo_web_app.templates.ModelsPage
import me.senseiju.cosmo_web_app.utils.FileType
import me.senseiju.cosmo_web_app.utils.getLoginSession
import me.senseiju.cosmo_web_app.utils.isFileOfType
import java.io.File

fun Route.userModels() {
    route("/models") {
        handle {
            val loginSession = getLoginSession(call, AppPath.AUTH) ?: return@handle

            val user = getDiscordUser(loginSession.accessToken)
            if (user.id == null) {
                call.respondRedirect("${AppPath.AUTH}")
                return@handle
            }

            call.respondHtmlTemplate(ModelsPage(user, emptyList())) {}
        }

        route("/new-model") {
            post {
                val multipart = call.receiveMultipart()

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> parseFileItem(part)
                        is PartData.FormItem -> parseFormItem(part)
                        else -> println("Invalid PartData received")
                    }

                    part.dispose()
                }

                call.respondRedirect("/models")
            }
        }
    }
}

private fun parseFileItem(part: PartData.FileItem) {
    val name = part.originalFileName
    val file = File(TEMP_PATH, "$name")

    part.streamProvider().use { stream ->
        file.outputStream().buffered().use {
            stream.copyTo(it)
        }
    }

    if (!file.isFileOfType(FileType.PNG)) {
        file.delete()

        println("Deleting file, it is not of type ${FileType.PNG}")
    }
}

private fun parseFormItem(part: PartData.FormItem) {
    println("${part.name} = ${part.value}")
}