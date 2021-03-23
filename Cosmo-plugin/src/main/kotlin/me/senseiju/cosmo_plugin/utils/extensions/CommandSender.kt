package me.senseiju.sennetmc.utils.extensions

import me.senseiju.cosmo_plugin.Cosmo
import me.senseiju.cosmo_plugin.utils.PlaceholderSet
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

private val messagesFile = JavaPlugin.getPlugin(Cosmo::class.java).messagesFile

fun CommandSender.message(s: String) {
    this.sendMessage(s.color())
}

fun CommandSender.message(list: List<String>) {
    list.forEach { this.message(it) }
}

fun CommandSender.sendConfigMessage(messageName: String, vararg replacements: PlaceholderSet = emptyArray()) {
    this.sendConfigMessage(messageName, prefix = true, replacements = replacements)
}

fun CommandSender.sendConfigMessage(
    messageName: String,
    prefix: Boolean = true,
    vararg replacements: PlaceholderSet = emptyArray()
) {
    val config = messagesFile.config

    if (config.isString(messageName)) {
        var message = config.getString(messageName, "Undefined message for '$messageName'")!!

        for (replacement in replacements) {
            message = message.replace(replacement.placeholder, replacement.value.toString())
        }

        if (prefix) {
            message = "${config.getString("PREFIX", "#914ef5&lSennetMC »")} $message"
        }

        this.message(message)
    } else {
        val messages = config.getStringList(messageName)
        val messagesToSend = ArrayList<String>()

        for (message in messages) {
            var line = message
            for (replacement in replacements) {
                line = line.replace(replacement.placeholder, replacement.value.toString())
            }

            if (prefix) {
                line = "${config.getString("PREFIX", "#914ef5&lSennetMC »")} $line"
            }
            messagesToSend.add(line)
        }

        this.message(messagesToSend)
    }
}