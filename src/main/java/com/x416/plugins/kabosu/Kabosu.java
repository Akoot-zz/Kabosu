package com.x416.plugins.kabosu;

import com.x416.plugins.kabosu.commands.BackupCommand;
import com.x416.plugins.kabosu.commands.ReloadCommand;
import com.x416.plugins.kabosu.commands.SaveCommand;
import com.x416.plugins.kabosu.tasks.BackupTask;
import com.x416.plugins.kabosu.tasks.SaveTask;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Kabosu extends JavaPlugin {

    private BackupTask backupTask;
    private SaveTask saveTask;
    private File backupDirectory;

    public BackupTask getBackupTask() {
        return backupTask;
    }

    public File getBackupDirectory() {
        return backupDirectory;
    }

    public SaveTask getSaveTask() {
        return saveTask;
    }

    @Override
    public void onEnable() {

        // Load config
        try {
            getConfig().load("config.yml");
        } catch (IOException | InvalidConfigurationException e) {
            getConfig().options().copyDefaults(true);
            try {
                getConfig().save(new File(getDataFolder(), "config.yml"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        // Generate backup directory
        backupDirectory = new File(getConfig().getString("backup-directory"));
        backupDirectory.mkdirs();

        // Setup tasks
        backupTask = new BackupTask(this);
        saveTask = new SaveTask(this);

        // Convert minutes to ticks
        long saveInterval = (long) (getConfig().getInt("intervals.save") * 60) * 20L;
        long backupInterval = (long) (getConfig().getInt("intervals.backup") * 60) * 20L;

        // Schedule tasks
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, saveTask, saveInterval, saveInterval);
        getServer().getScheduler().scheduleAsyncRepeatingTask(this, backupTask, saveInterval, backupInterval);

        // Register commands
        getCommand("save").setExecutor(new SaveCommand(this));
        getCommand("backup").setExecutor(new BackupCommand(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}