package me.senseiju.cosmo_plugin.http

import com.sun.net.httpserver.HttpServer
import me.senseiju.cosmo_plugin.Cosmo
import java.net.InetSocketAddress

class InternalHttpServer(plugin: Cosmo) {
    val port = plugin.configFile.config.getInt("internal-http-port", 8080)

    private val server = HttpServer.create(InetSocketAddress("localhost", port), 0)

    init {
        server.createContext("/cosmo", RequestHandler(plugin))
        server.start()
    }

    fun stop() {
        server.stop(0)
    }
}