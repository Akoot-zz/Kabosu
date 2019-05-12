package com.x416.plugins.kabosu.commands;

import com.x416.plugins.kabosu.Kabosu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class KabosuCommand implements CommandExecutor, TabExecutor {

    protected final Kabosu plugin;

    public KabosuCommand(Kabosu plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Kabosu!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completion = new ArrayList<>();
        return completion;
    }
}
