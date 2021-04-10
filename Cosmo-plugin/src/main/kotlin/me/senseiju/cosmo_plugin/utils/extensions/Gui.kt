package me.senseiju.cosmo_plugin.utils.extensions

import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.PaginatedGui
import org.bukkit.Material

fun defaultPaginatedGuiTemplate(row: Int, pageSize: Int, name: String): PaginatedGui {
    val gui = PaginatedGui(row, pageSize, name.color())

    gui.setDefaultClickAction { it.isCancelled = true }

    gui.setItem(6, 4, ItemBuilder.from(Material.PAPER)
        .setName("&aPrevious".color())
        .asGuiItem { gui.previous() })
    gui.setItem(6, 6, ItemBuilder.from(Material.PAPER)
        .setName("&aNext".color())
        .asGuiItem { gui.next() })

    gui.filler.fillBottom(
        ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE)
            .setName(" ")
            .asGuiItem()
    )

    return gui
}

fun defaultGuiTemplate(row: Int, name: String): Gui {
    val gui = Gui(row, name.color())

    gui.setDefaultClickAction { it.isCancelled = true }

    return gui
}