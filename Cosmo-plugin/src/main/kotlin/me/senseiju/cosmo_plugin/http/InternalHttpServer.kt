package me.senseiju.cosmo_plugin.http

import com.sun.net.httpserver.HttpServer
import me.senseiju.cosmo_plugin.Cosmo
import java.net.InetSocketAddress
import java.net.URL

class InternalHttpServer(plugin: Cosmo) {
    val port = plugin.configFile.config.getInt("internal-http-port", 8080)
    var ip: String = URL("https://checkip.amazonaws.com").readText()

    private val server = HttpServer.create(InetSocketAddress("0.0.0.0", port), 0)

    init {
        server.createContext("/cosmo", RequestHandler(plugin))
        server.executor = null
        server.start()
    }

    fun stop() {
        server.stop(0)
    }
}