package me.senseiju.cosmo_web_app.routes.api

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.data_storage.*
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelDataType
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.sessions.LoginSession
import java.util.*

fun Route.apiPacks() {
    route("/packs") {
        deletePack()
        postPack()

        route("/models") {
            deletePackModel()
            postPackModel()
        }
    }
}

private fun Route.postPack() {
    post {
        val loginSession = call.sessions.get<LoginSession>() ?: return@post

        val user = getDiscordUser(loginSession.accessToken)
        if (user.id == null) {
            return@post
        }

        val name = call.parameters["name"] ?: return@post

        insertPack(name, user.id)
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

        val packId = try {
            UUID.fromString(call.parameters["pack_id"])
        } catch (e: Exception) {
            return@post
        }

        val modelData = call.parameters["model_data"]?.toIntOrNull() ?: return@post

        val modelType = ModelType.parse(call.parameters["model_type"] ?: "") ?: return@post

        insertModelToPack(packId, ModelDataType(modelData, modelType))
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

        deleteModelsFromPack(packId, ModelDataType(modelData, modelType))
    }
}