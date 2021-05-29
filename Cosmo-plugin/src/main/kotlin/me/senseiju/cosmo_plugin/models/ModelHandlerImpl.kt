package me.senseiju.cosmo_plugin.models

import org.bukkit.entity.Player

interface ModelHandlerImpl {
    fun update(player: Player, targets: Collection<Player>)
}