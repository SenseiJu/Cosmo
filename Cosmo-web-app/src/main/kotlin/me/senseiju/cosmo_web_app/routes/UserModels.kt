package me.senseiju.cosmo_web_app.routes

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.AppPath
import me.senseiju.cosmo_web_app.MODELS_PATH
import me.senseiju.cosmo_web_app.TEMP_PATH
import me.senseiju.cosmo_web_app.data_storage.insertModel
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.pack_builder.json_templates.ItemJsonTemplate
import me.senseiju.cosmo_web_app.templates.ModelsPage
import me.senseiju.cosmo_web_app.templates.NewModelPage
import me.senseiju.cosmo_web_app.utils.FileType
import me.senseiju.cosmo_web_app.utils.defaultScope
import me.senseiju.cosmo_web_app.utils.getLoginSession
import me.senseiju.cosmo_web_app.utils.isFileOfType
import java.io.File
import java.util.*

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
            handle {
                val loginSession = getLoginSession(call, AppPath.AUTH) ?: return@handle

                val user = getDiscordUser(loginSession.accessToken)
                if (user.id == null) {
                    call.respondRedirect("${AppPath.AUTH}")
                    return@handle
                }

                call.respondHtmlTemplate(NewModelPage(user)) {}
            }

            post {
                val loginSession = getLoginSession(call, AppPath.AUTH) ?: return@post

                val user = getDiscordUser(loginSession.accessToken)
                if (user.id == null) {
                    call.respondRedirect("${AppPath.AUTH}")
                    return@post
                }

                val multipart = call.receiveMultipart()
                val tempDir = File("$TEMP_PATH/${UUID.randomUUID()}")
                tempDir.mkdirs()

                val newModelCheckList = NewModelFileCheckList(user.id)

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> parseFileItem(part, newModelCheckList, tempDir)
                        is PartData.FormItem -> parseFormItem(part, newModelCheckList)
                        else -> println("Invalid PartData received")
                    }

                    part.dispose()
                }

                if (newModelCheckList.areAllRequirementsMade()) {
                    newModelCheckList.submitModel()
                }

                tempDir.deleteRecursively()

                call.respondRedirect("/models")
            }
        }
    }
}

private fun parseFileItem(part: PartData.FileItem, modelFileCheckList: NewModelFileCheckList, tempDir: File) {
    val fileName = part.originalFileName ?: return
    if (fileName.isBlank()) {
        return
    }
    val file = File(tempDir, fileName)

    part.streamProvider().use { stream ->
        file.outputStream().buffered().use {
            stream.copyTo(it)
        }
    }

    when (part.name) {
        "model_item_json" -> {
            try {
                Json.decodeFromString<ItemJsonTemplate>(file.readText())

                modelFileCheckList.itemJsonFile = file
            } catch (e: SerializationException) {
                e.printStackTrace()
                file.delete()
            }
        }
        "model_textures" -> {
            if (!file.isFileOfType(FileType.PNG) || file.length() == 0L) {
                file.delete()
            } else {
                modelFileCheckList.textureFiles.add(file)
            }
        }
        "model_display_image" -> {
            if (!file.isFileOfType(FileType.PNG) || file.length() == 0L) {
                file.delete()
            } else {
                modelFileCheckList.displayImageFile = file
            }
        }
        else -> {
            file.delete()
        }
    }
}

private fun parseFormItem(part: PartData.FormItem, modelFileCheckList: NewModelFileCheckList) {
    when (part.name) {
        "name" -> {
            if (isNameValid(part.value)) {
                modelFileCheckList.name = part.value
            }
        }
        "model_type" -> {
            modelFileCheckList.modelType = ModelType.parse(part.value) ?: return
        }
    }
}

private fun isNameValid(nameToTest: String): Boolean {
    return nameToTest.length in 4..32
}

private class NewModelFileCheckList(private val userId: String) {
    lateinit var name: String
    lateinit var modelType: ModelType
    lateinit var itemJsonFile: File
    lateinit var displayImageFile: File

    val textureFiles = arrayListOf<File>()

    fun areAllRequirementsMade(): Boolean {
        return ::name.isInitialized
                && ::modelType.isInitialized
                && ::itemJsonFile.isInitialized
                && ::displayImageFile.isInitialized
                && textureFiles.isNotEmpty()
    }

    fun submitModel() {
        val modelData = insertModel(modelType, name, userId)

        val modelsDir = File(MODELS_PATH, "${modelType.toString().toLowerCase()}/$modelData")
        modelsDir.mkdirs()

        itemJsonFile.copyTo(File(modelsDir, "$modelData.json"))
        displayImageFile.copyTo(File(modelsDir, "$modelData.png"))

        val texturesDir = File(modelsDir, "textures")
        texturesDir.mkdirs()

        textureFiles.forEach { it.copyTo(File(texturesDir, it.name)) }
    }
}