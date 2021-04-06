package me.senseiju.cosmo_web_app.routes.api

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.sessions.*
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.data_storage.deleteModelsFromResourcePack
import me.senseiju.cosmo_web_app.data_storage.isUserPackOwner
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelDataType
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUser
import me.senseiju.cosmo_web_app.sessions.LoginSession
import java.util.*

fun Route.apiPacks() {
    route("/packs") {
        delete {

        }

        delete("/models") {
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

            deleteModelsFromResourcePack(packId, ModelDataType(modelData, modelType))
        }
    }
}