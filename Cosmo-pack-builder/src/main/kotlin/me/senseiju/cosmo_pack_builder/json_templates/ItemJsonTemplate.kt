package me.senseiju.cosmo_pack_builder.json_templates

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemJsonTemplate(
    var credit: String? = null,
    var texture_size: Collection<Int>? = null,
    var textures: Map<String, String>? = null,
    var elements: Collection<ItemElementJsonElement>? = null,
    var display: ItemDisplaysJsonElement,
    var groups: List<ItemGroupJsonElement>? = null
    )

@Serializable
data class ItemElementJsonElement(
    var name: String? = null,
    var from: Collection<Float>? = null,
    var to: Collection<Float>? = null,
    var rotation: ItemRotationJsonElement? = null,
    var color: Int? = null,
    var faces: ItemFacesJsonElement
    )

@Serializable
data class ItemRotationJsonElement(
    var angle: Float? = null,
    var axis: String? = null,
    var origin: Collection<Float>? = null,
    var rescale: Boolean? = null
    )

@Serializable
data class ItemFacesJsonElement(
    var north: ItemFaceJsonElement,
    var south: ItemFaceJsonElement,
    var east: ItemFaceJsonElement,
    var west: ItemFaceJsonElement,
    var up: ItemFaceJsonElement,
    var down: ItemFaceJsonElement
    )

@Serializable
data class ItemFaceJsonElement(
    var uv: Collection<Float>? = null,
    var rotation: Float? = null,
    var texture: String? = null
    )

@Serializable
data class ItemGroupJsonElement(
    var name: String? = null,
    var origin: Collection<Float>? = null,
    var children: Collection<Int>? = null
    )

@Serializable
data class ItemDisplaysJsonElement(
    @SerialName("firstperson_lefthand") var firstPersonLeftHand: ItemDisplayJsonElement? = null,
    @SerialName("thirdperson_lefthand") var thirdPersonLeftHand: ItemDisplayJsonElement? = null,
    @SerialName("firstperson_righthand") var firstPersonRightHand: ItemDisplayJsonElement? = null,
    @SerialName("thirdperson_righthand") var thirdPersonRightHand: ItemDisplayJsonElement? = null,
    var ground: ItemDisplayJsonElement? = null,
    var gui: ItemDisplayJsonElement? = null,
    var head: ItemDisplayJsonElement? = null
    )

@Serializable
data class ItemDisplayJsonElement(
    var rotation: Collection<Float>? = null,
    var translation: Collection<Float>? = null,
    var scale: Collection<Float>? = null
    )