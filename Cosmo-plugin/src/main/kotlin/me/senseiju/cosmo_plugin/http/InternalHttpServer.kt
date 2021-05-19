package me.senseiju.cosmo_plugin.http

import com.sun.net.httpserver.HttpServer
import me.senseiju.cosmo_plugin.Cosmo
import java.net.InetSocketAddress
import java.net.URL

class InternalHttpServer(plugin: Cosmo) {
    val port = plugin.configFile.config.getInt("internal-http-port", 8080)
    var ip: String = URL("https://checkip.amazonaws.com").readText()
    var isEnabled = plugin.configFile.config.getBoolean("use-internal-http-server", false)
        private set

    private lateinit var server: HttpServer

    init {
        if (isEnabled) {
            server = HttpServer.create(InetSocketAddress("0.0.0.0", port), 0)
            server.createContext("/cosmo", RequestHandler(plugin))
            server.executor = null
            server.start()
        }
    }

    fun stop() {
        if (isEnabled) {
            server.stop(0)
        }
    }
}