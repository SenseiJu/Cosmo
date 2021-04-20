package me.senseiju.cosmo_web_app.routes.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.PACK_PATH
import me.senseiju.cosmo_web_app.data_storage.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.pack_builder.buildPack
import me.senseiju.cosmo_web_app.sessions.LoginSession
import java.io.File
import java.util.*

fun Route.apiPacks() {
    route("/packs") {
        deletePack()
        postPack()
        getPack()

        route("/models") {
            deletePackModel()
            postPackModel()
        }
    }
}

private fun Route.getPack() {
    get("{packId}") {
        val packId = try {
            UUID.fromString(call.parameters["packId"])
        } catch (e: Exception) {
            call.respondText("Pack with specified UUID does not exist", status = HttpStatusCode.BadRequest)
            return@get
        }

        val packZip = File("$PACK_PATH/$packId.zip")
        val packSha1 = File("$PACK_PATH/$packId.sha1")
        val packJson = File("$PACK_PATH/$packId.json")

        if (!(packZip.exists() && packSha1.exists() && packJson.exists())) {
            call.respondText("Pack with specified UUID does not exist", status = HttpStatusCode.BadRequest)
            return@get
        }

        when (call.request.queryParameters["type"]) {
            "zip" -> sendPackZip(call, packZip)
            "sha1" -> call.respondFile(packSha1)
            "json" -> call.respondFile(packJson)
            else -> {
                call.respondText("Invalid file type supplied", status = HttpStatusCode.BadRequest)
                return@get
            }
        }
    }
}

private suspend fun sendPackZip(call: ApplicationCall, packZip: File) {
    call.response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(
            ContentDisposition.Parameters.FileName, "pack.zip"
        ).toString()
    )

    call.respondFile(packZip)
}

private fun Route.postPack() {
    post {
        val loginSession = call.sessions.get<LoginSession>() ?: return@post

        val user = getDiscordUser(loginSession.accessToken)
        if (user.id == null) {
            return@post
        }

        val packName = call.receiveParameters()["pack_name"] ?: return@post

        insertPack(packName, user.id)
    }
}

private fun Route.deletePack() {
    delete {
        val loginSession = call.sessions.get<LoginSession>() ?: return@delete

        val user = getDiscordUser(loginSession.accessToken)
        if (user.id == null) {
            return@delete
        }

        val packId = try {
            UUID.fromString(call.parameters["pack_id"])
        } catch (e: Exception) {
            return@delete
        }

        if (!isUserPackOwner(packId, user.id)) {
            return@delete
        }

        deletePack(packId)
    }
}

private fun Route.postPackModel() {
    post {
        val loginSession = call.sessions.get<LoginSession>() ?: return@post

        val user = getDiscordUser(loginSession.accessToken)
        if (user.id == null) {
            return@post
        }

        val paramters = call.receiveParameters()

        val packId = try {
            UUID.fromString(paramters["pack_id"])
        } catch (e: Exception) {
            return@post
        }

        val modelData = paramters["model_data"]?.toIntOrNull() ?: return@post
        val modelType = ModelType.parse(paramters["model_type"] ?: "") ?: return@post

        insertModelToPack(packId, ModelWrapper(modelData, modelType))
    }
}

private fun Route.deletePackModel() {
    delete {
        val loginSession = call.sessions.get<LoginSession>() ?: return@delete

        val user = getDiscordUser(loginSession.accessToken)
        if (user.id == null) {
            return@delete
        }

        val packId = try {
            UUID.fromString(call.parameters["pack_id"])
        } catch (e: Exception) {
            return@delete
        }

        if (!isUserPackOwner(packId, user.id)) {
            return@delete
        }

        val modelData = call.parameters["model_data"]?.toIntOrNull() ?: return@delete

        val modelType = ModelType.parse(call.parameters["model_type"] ?: "") ?: return@delete

        deleteModelsFromPack(packId, ModelWrapper(modelData, modelType))
    }
}