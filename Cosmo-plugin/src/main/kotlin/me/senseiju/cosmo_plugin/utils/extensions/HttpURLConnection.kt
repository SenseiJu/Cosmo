package me.senseiju.cosmo_plugin.utils.extensions

import java.net.HttpURLConnection

fun HttpURLConnection.setUserAgent(
    userAgent: String =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36"
) {
    this.setRequestProperty("User-Agent", userAgent)
}