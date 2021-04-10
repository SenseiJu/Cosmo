package me.senseiju.cosmo_web_app.pack_builder.json_templates

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.senseiju.cosmo_commons.ModelType

fun createModelJson(modelType: ModelType, modelData: Collection<Int>): ModelJsonTemplate {
    return ModelJsonTemplate(modelType.getParentJsonElement(), modelData.map { createOverrideJsonElement(modelType, it) })
}

private fun createOverrideJsonElement(modelType: ModelType, modelData: Int): OverrideJsonElement {
    return OverrideJsonElement(
        CustomModelDataJsonElement(modelData),
        "item/${modelType.toString().toLowerCase()}/$modelData"
    )
}

@Serializable
data class ModelJsonTemplate(val parent: String, val overrides: List<OverrideJsonElement>)

@Serializable
data class OverrideJsonElement(val predicate: CustomModelDataJsonElement, val model: String)

@Serializable
data class CustomModelDataJsonElement(@SerialName("custom_model_data") val customModelData: Int)


