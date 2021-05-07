package me.senseiju.cosmo_plugin.http

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import me.senseiju.cosmo_plugin.Cosmo
import java.io.File

class RequestHandler(plugin: Cosmo) : HttpHandler {
    private val file = File(plugin.dataFolder, "pack.zip")

    override fun handle(exchange: HttpExchange) {
        exchange.setAttribute("Content-Type", "application/zip");
        exchange.responseHeaders.add("Content-Disposition", "attachment; filename=pack.zip")
        exchange.sendResponseHeaders(200, file.length())
        exchange.responseBody.use {
            it.write(file.readBytes())
        }
    }
}