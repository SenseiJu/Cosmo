package me.senseiju.cosmo_plugin.utils.extensions

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun ItemStack.toBase64(): String {
    ByteArrayOutputStream().use {
        BukkitObjectOutputStream(it).use { dataOutputStream ->
            dataOutputStream.writeObject(this)
        }

        return Base64Coder.encodeLines(it.toByteArray())
    }
}

fun itemStackFromBase64(base64: String): ItemStack {
    ByteArrayInputStream(Base64Coder.decodeLines(base64)).use {
        BukkitObjectInputStream(it).use { dataInputStream ->
            return dataInputStream.readObject() as ItemStack
        }
    }
}