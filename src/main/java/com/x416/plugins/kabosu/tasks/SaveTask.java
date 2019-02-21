package com.x416.plugins.kabosu.tasks;

import com.x416.plugins.kabosu.Kabosu;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public class SaveTask extends BukkitRunnable {

    private final Kabosu plugin;
    private final Logger log = Logger.getLogger(getClass().getName());

    public SaveTask(Kabosu plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves all of the worlds and player data
     */
    public void save() {

        // Begin save message
        if (plugin.getConfig().getBoolean("broadcast-saves")) {
            plugin.getServer().broadcastMessage(plugin.getConfig().getString("strings.save.start"));
        }

        // Save players
        plugin.getServer().savePlayers();

        // Save worlds
        for (World world : plugin.getServer().getWorlds()) {
            if (!world.isAutoSave()) {
                world.save();
            }
        }

        // Finish save message
        if (plugin.getConfig().getBoolean("broadcast-saves")) {
            plugin.getServer().broadcastMessage(plugin.getConfig().getString("strings.save.end"));
        }
    }

    @Override
    public void run() {

        // Only run automated tasks if there are players online
        if (plugin.getServer().getOnlinePlayers().size() > 0) {
            save();
        }
    }
}
