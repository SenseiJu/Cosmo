package me.senseiju.cosmo_pack_builder.json_templates

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.senseiju.cosmo_commons.ModelType

fun createModelJson(modelType: ModelType, modelData: Collection<Int>): ModelJsonTemplate {
    return ModelJsonTemplate(modelType.getParentJsonElement(), modelData.map { createOverrideJsonElement(it) })
}

private fun createOverrideJsonElement(modelData: Int): OverrideJsonElement {
    return OverrideJsonElement(CustomModelDataJsonElement(modelData), "item/$modelData")
}

@Serializable
data class ModelJsonTemplate(val parent: String, val overrides: List<OverrideJsonElement>)

@Serializable
data class OverrideJsonElement(val predicate: CustomModelDataJsonElement, val model: String)

@Serializable
data class CustomModelDataJsonElement(@SerialName("custom_model_data") val customModelData: Int)


