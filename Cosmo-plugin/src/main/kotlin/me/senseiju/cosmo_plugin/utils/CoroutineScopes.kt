package me.senseiju.cosmo_plugin.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val defaultScope = CoroutineScope(Dispatchers.Default)

fun useCoroutine(use: Boolean, block: () -> Unit) {
    if (use) {
        defaultScope.launch { block.invoke() }
    } else {
        block.invoke()
    }
}