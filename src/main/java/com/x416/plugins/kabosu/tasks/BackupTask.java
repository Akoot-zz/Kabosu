package com.x416.plugins.kabosu.tasks;

import com.x416.plugins.kabosu.Kabosu;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

public class BackupTask extends BukkitRunnable {

    private final Kabosu plugin;
    private final Logger log = Logger.getLogger(getClass().getName());
    private final ZipParameters zipParameters = new ZipParameters();

    public BackupTask(Kabosu plugin) {
        this.plugin = plugin;
    }

    /**
     * Backs up all of the worlds
     */
    public void backup() {

        // Delete oldest backup
        File[] backups = plugin.getBackupDirectory().listFiles();
        assert backups != null;
        if (backups.length >= plugin.getConfig().getInt("backups-on-file")) {
            long[] modified = new long[backups.length];
            for (int i = 0; i < backups.length; i++) {
                modified[i] = backups[i].lastModified();
            }
            Arrays.sort(modified);
            for (File file : backups) {
                if (file.lastModified() == modified[0]) {
                    file.delete();
                    break;
                }
            }
        }

        // Initialize zip file
        ZipFile zipFile = null;
        DateFormat df = new SimpleDateFormat(plugin.getConfig().getString("backup-format"));
        Date date = new Date(System.currentTimeMillis());
        String backupFileName = "backup-" + df.format(date) + ".zip";
        File backupFile = new File(plugin.getBackupDirectory(), backupFileName);
        try {
            zipFile = new ZipFile(backupFile);
        } catch (ZipException e) {
            plugin.getLogger().severe("Error creating backup zip!");
            e.printStackTrace();
            plugin.getPluginLoader().disablePlugin(plugin);
        }

        // Begin backup message
        if (plugin.getConfig().getBoolean("broadcast-backups")) {
            plugin.getServer().broadcastMessage(plugin.getConfig().getString("strings.backup.start"));
        }

        // Add worlds to backup.zip
        try {
            for (World world : plugin.getServer().getWorlds()) {
                assert zipFile != null;
                zipFile.addFolder(world.getWorldFolder(), zipParameters);
            }
        } catch (ZipException e) {
            log.severe("Error trying to backup worlds.");
            e.printStackTrace();
        }

        // Finish backup message
        if (plugin.getConfig().getBoolean("broadcast-backups")) {
            plugin.getServer().broadcastMessage(plugin.getConfig().getString("strings.backup.end"));
        }
    }

    @Override
    public void run() {

        // Only run automated tasks if there are players online
        if (plugin.getServer().getOnlinePlayers().size() > 0) {
            backup();
        }
    }
}
