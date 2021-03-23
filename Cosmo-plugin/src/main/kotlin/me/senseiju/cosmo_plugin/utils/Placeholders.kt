package me.senseiju.cosmo_plugin.utils

fun applyPlaceholders(string: String, vararg replacements: PlaceholderSet = emptyArray()): String {
    var replacedString = string

    replacements.forEach {
        replacedString = replacedString.replace(it.placeholder, it.value.toString())
    }

    return replacedString
}

fun applyPlaceholders(strings: List<String>, vararg replacements: PlaceholderSet = emptyArray()): List<String> {
    val replacedStrings = ArrayList<String>()

    strings.forEach string@{ string ->

        replacements.forEach { replacement ->
            if (!string.contains(replacement.placeholder)) {
                return@forEach
            }

            if (replacement.value is List<*>) {
                replacedStrings.addAll(replacement.value.map { it.toString() })
                return@string
            }

            replacedStrings.add(string.replace(replacement.placeholder, replacement.value.toString()))
            return@string
        }

        replacedStrings.add(string)
    }

    return replacedStrings
}