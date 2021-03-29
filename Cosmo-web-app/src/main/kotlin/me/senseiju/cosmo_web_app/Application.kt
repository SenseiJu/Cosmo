package me.senseiju.cosmo_web_app

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import java.io.File
import java.util.*

const val PACK_DIR = "D:\\Intellij Projects\\Cosmo\\Cosmo-web-app\\src\\test\\resources\\"

@kotlin.jvm.JvmOverloads
fun Application.cosmo(testing: Boolean = false) {

    install(ContentNegotiation) {
        json()
    }

    routing {
        getResourcePack()
    }
}

fun Route.getResourcePack() {
    route("/") {
        get("{packId}") {
            val packId = try {
                UUID.fromString(call.parameters["packId"])
            } catch (e: Exception) {
                call.respondText("Pack with specified UUID does not exist", status = HttpStatusCode.BadRequest)
                return@get
            }

            val packZip = File("$PACK_DIR/$packId.zip")
            val packSha1 = File("$PACK_DIR/$packId.sha1")
            val packJson = File("$PACK_DIR/$packId.json")

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