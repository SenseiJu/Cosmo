package me.senseiju.cosmo_web_app.data_storage.wrappers

import kotlinx.serialization.Serializable
import me.senseiju.cosmo_commons.ModelType

@Serializable
data class ModelWrapper(
    val modelData: Int,
    val modelType: ModelType,
    val name: String? = null,
    val author: String? = null
)
