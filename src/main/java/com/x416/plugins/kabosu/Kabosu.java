package com.x416.plugins.kabosu;

import com.x416.plugins.kabosu.commands.KabosuCommand;
import com.x416.plugins.kabosu.commands.TaskCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Kabosu extends JavaPlugin {

    public List<Task> tasks;

    @Override
    public void onEnable() {

        // Create tasks
        tasks = new ArrayList<>();

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

        // Start all saved tasks
        for (String key : getConfig().getKeys(true)) {
            if (key.matches("tasks\\.\\w+")) {
                Task task = new Task(this);
                task.name = key.substring(key.indexOf(".") + 1);
                task.interval = getConfig().getLong(key + ".interval");
                task.setCommand(getConfig().getString(key + ".command"));
                task.repeating = getConfig().getBoolean(key + ".repeating");
                task.start();
                tasks.add(task);
            }
        }

        // Register commands
        getCommand("kabosu").setExecutor(new KabosuCommand(this));
        getCommand("task").setExecutor(new TaskCommand(this));
    }

    public Task getTask(String name) {
        for (Task task : tasks) {
            if (task.name.equals(name)) {
                return task;
            }
        }
        return null;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}