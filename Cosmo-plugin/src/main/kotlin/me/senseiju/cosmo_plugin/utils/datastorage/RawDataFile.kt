package me.senseiju.cosmo_plugin.utils.datastorage

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileWriter
import java.util.*

class RawDataFile(private val plugin: JavaPlugin, private val path: String) {

    private lateinit var file: File

    init {
        reload()
    }

    fun reload() {
        file = File("${plugin.dataFolder}/${path}")

        if (!file.exists()) {
            plugin.dataFolder.mkdirs()
            file.createNewFile()
        }
    }

    fun write(s: String) {
        val writer = FileWriter(file)
        writer.write(s)
        writer.close()
    }

    fun read(): String {
        val reader = Scanner(file)

        return if (reader.hasNextLine()) {
            reader.nextLine()
        } else {
            ""
        }
    }
}