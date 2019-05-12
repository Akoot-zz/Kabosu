package com.x416.plugins.kabosu;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Task {

    public String name;
    public long interval;
    public boolean repeating;
    private BukkitTask task;
    private BukkitRunnable runnable;
    private String command;

    private Kabosu plugin;

    public Task(Kabosu plugin) {
        this.plugin = plugin;
    }

    public static long getInterval(String s) {
        long interval = 0;
        int time;
        try {
            time = Integer.parseInt(s.substring(0, s.length() - 1));
        } catch (NumberFormatException e) {
            time = 0;
        }
        char c = s.toCharArray()[s.length() - 1];
        switch (c) {
            case 't':
                interval = (long) time;
                break;
            case 's':
                interval = ((long) time) * 20L;
                break;
            case 'm':
                interval = ((long) (time * 60)) * 20L;
                break;
            case 'h':
                interval = ((long) (time * 3600)) * 20L;
                break;
        }
        return interval;
    }

    public void start() {
        if (repeating) {
            task = runnable.runTaskTimer(plugin, interval, interval);
        } else {
            task = runnable.runTaskLater(plugin, interval);
        }
        plugin.getConfig().set("tasks." + name + ".interval", interval);
        plugin.getConfig().set("tasks." + name + ".repeating", repeating);
        plugin.getConfig().set("tasks." + name + ".command", command);
        plugin.saveConfig();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        };
    }

    public void execute() {
        runnable.run();
    }

    public void cancel() {
        task.cancel();
        plugin.getConfig().set("tasks." + name, null);
        plugin.saveConfig();
    }

    @Override
    public String toString() {
        return name + ": /" + (command != null ? command : "<none>") + " " + (repeating ? "every" : "after") + " " + interval + " ticks";
    }
}
