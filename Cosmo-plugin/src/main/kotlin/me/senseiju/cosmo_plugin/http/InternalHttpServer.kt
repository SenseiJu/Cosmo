package me.senseiju.cosmo_plugin.http

import com.sun.net.httpserver.HttpServer
import me.senseiju.cosmo_plugin.Cosmo
import java.net.InetSocketAddress

class InternalHttpServer(plugin: Cosmo) {
    private val server = HttpServer.create(InetSocketAddress("localhost", 8080), 0)

    init {
        server.createContext("/cosmo", RequestHandler(plugin))
        server.start()
    }

    fun stop() {
        server.stop(0)
    }
}