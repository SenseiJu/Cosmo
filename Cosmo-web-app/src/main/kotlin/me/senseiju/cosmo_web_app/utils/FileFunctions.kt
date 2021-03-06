package me.senseiju.cosmo_web_app.utils

import java.io.File
import java.util.stream.IntStream.range


enum class FileType(vararg val bytes: Int) {
    PNG(0x89, 0x50, 0x4E, 0x47);
}

fun File.isFileOfType(fileType: FileType): Boolean {
    inputStream().use {
        val header = it.readNBytes(fileType.bytes.size)

        for (i in range(0, fileType.bytes.size)) {
            if (header[i] != fileType.bytes[i].toByte()) {
                return false
            }
        }

        return true
    }
}