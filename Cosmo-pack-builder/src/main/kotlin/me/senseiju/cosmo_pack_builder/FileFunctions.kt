package me.senseiju.cosmo_pack_builder

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_pack_builder.json_templates.createPackMcmetaJson
import net.lingala.zip4j.ZipFile
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.util.*

fun createMcmeta(packId: UUID) {
    File("$PACK_TEMP_DIR/$packId/pack.mcmeta").writeText(Json.encodeToString(createPackMcmetaJson()))
}

fun deleteDir(packId: UUID) {
    File("$PACK_TEMP_DIR/$packId").deleteRecursively()
}

fun createZip(packId: UUID) {
    val zipFile = ZipFile(File("$PACK_TEMP_DIR/$packId.zip"))

    zipFile.addFolder(File("$PACK_TEMP_DIR/$packId/assets"))
    zipFile.addFile(File("$PACK_TEMP_DIR/$packId/pack.mcmeta"))
}

fun createZipSHA1(packId: UUID) {
    File("$PACK_TEMP_DIR/$packId.sha1").writeText(digestFileToSHA1("$PACK_TEMP_DIR/$packId.zip"))
}

private fun digestFileToSHA1(path: String): String {
    return DigestUtils.sha1Hex(File(path).inputStream())
}