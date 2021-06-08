package me.senseiju.cosmo_plugin.utils.extensions

import java.util.logging.Logger

fun Logger.debug(msg: String) {
    this.info("Debug: $msg")
}