package me.senseiju.cosmo_web_app

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import java.io.File

@kotlin.jvm.JvmOverloads
fun Application.cosmo(testing: Boolean = false) {

    install(ContentNegotiation) {
        json()
    }

    install(Authentication) {
        basic("test") {
            validate { credentials ->
                if (credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    routing {
        authenticate("test") {
            getResourcePack()
        }
    }
}

fun Route.getResourcePack() {
    route("/") {
        get("{resourcePackUUID}") {
            val resourcePackUUID = call.parameters["resourcePackUUID"] ?: return@get call.respondText(
                "Shitty UUID supplied :/",
                status = HttpStatusCode.BadRequest
            )

            val downloadPack = call.request.queryParameters["dl"]
            if (downloadPack.equals("1")) {
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName, "pack.zip"
                    ).toString()
                )

                call.respondFile(
                    File("D:\\Intellij Projects\\Cosmo\\Cosmo-web-app\\src\\test\\resources\\$resourcePackUUID\\pack.zip")
                )

                return@get
            }

            val requestDigest = call.request.queryParameters["digest"]
            if (requestDigest.equals("1")) {
                call.respondFile(
                    File("D:\\Intellij Projects\\Cosmo\\Cosmo-web-app\\src\\test\\resources\\$resourcePackUUID\\pack.sha1")
                )

                return@get
            }

            call.respondFile(
                File("D:\\Intellij Projects\\Cosmo\\Cosmo-web-app\\src\\test\\resources\\$resourcePackUUID\\pack.json")
            )
        }
    }
}