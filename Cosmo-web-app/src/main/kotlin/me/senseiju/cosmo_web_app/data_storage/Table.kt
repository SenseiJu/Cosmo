package me.senseiju.cosmo_web_app.data_storage

enum class Table {
    MODELS,
    RESOURCE_PACKS,
    RESOURCE_PACK_MODELS;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}