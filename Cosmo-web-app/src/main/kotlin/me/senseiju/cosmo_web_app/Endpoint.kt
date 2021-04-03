package me.senseiju.cosmo_web_app

private const val BASE_URL = "http://cosmo.senseiju.me:8080"

enum class Endpoint(private val url: String) {
    AUTH("/auth"),
    AWAY("/away");

    override fun toString(): String {
        return "$BASE_URL/$url"
    }
}