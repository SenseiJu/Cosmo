package com.codingforcookies.armorequip;

import me.senseiju.cosmo_plugin.utils.datastorage.DataFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ArmorEquip {

    public ArmorEquip(JavaPlugin plugin) {
        final DataFile blockedFile = new DataFile(plugin, "armor-equip-blocked.yml", true);

        plugin.getServer().getPluginManager().registerEvents(new ArmorListener(blockedFile.getConfig().getStringList("blocked")), plugin);

        try {
            Class.forName("org.bukkit.event.block.BlockDispenseArmorEvent");

            plugin.getServer().getPluginManager().registerEvents(new DispenserArmorListener(), plugin);
        } catch (Exception ignored) {
        }
    }
}