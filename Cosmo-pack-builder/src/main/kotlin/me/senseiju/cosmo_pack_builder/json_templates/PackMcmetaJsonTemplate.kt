package me.senseiju.cosmo_pack_builder.json_templates

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val PACK_FORMAT = 6
const val DESCRIPTION = "Resource pack provided and built by Cosmo"

fun createPackMcmetaJson(packFormat: Int = PACK_FORMAT, description: String = DESCRIPTION): PackMcmetaJsonTemplate {
    return PackMcmetaJsonTemplate(PackMcmetaElement(packFormat, description))
}

@Serializable
data class PackMcmetaJsonTemplate(private val pack: PackMcmetaElement)

@Serializable
data class PackMcmetaElement(
    @SerialName("pack_format") private val packFormat: Int,
    private val description: String
    )