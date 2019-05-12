package com.x416.plugins.kabosu.commands;

import com.x416.plugins.kabosu.Kabosu;
import com.x416.plugins.kabosu.Task;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TaskCommand extends KabosuCommand implements CommandExecutor, TabCompleter {

    public TaskCommand(Kabosu plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                for (Task task : plugin.tasks) {
                    sender.sendMessage("- " + task.toString());
                }
                return true;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete")) {
                Task task = plugin.getTask(args[1]);
                if (task != null) {
                    task.cancel();
                    plugin.tasks.remove(task);
                    sender.sendMessage("Task deleted!");
                } else {
                    sender.sendMessage("Task not found!");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("execute")) {
                Task task = plugin.getTask(args[1]);
                if (task != null) {
                    task.execute();
                    sender.sendMessage("Task executed!");
                } else {
                    sender.sendMessage("Task not found!");
                }
                return true;
            }
        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("init")) {
                Task task = plugin.getTask(args[1]);
                if (task != null) {
                    String s = "";
                    for (int i = 2; i < args.length; i++) {
                        s += args[i] + " ";
                    }
                    task.setCommand(s.trim());
                    task.start();
                    sender.sendMessage("Task initiated!");
                } else {
                    sender.sendMessage("Task not found!");
                }
                return true;
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("create")) {
                    Task task = new Task(plugin);
                    task.name = args[1];
                    task.interval = Task.getInterval(args[2]);
                    if (args[3].equalsIgnoreCase("true")) {
                        task.repeating = true;
                    } else if (args[3].equalsIgnoreCase("false")) {
                        task.repeating = false;
                    } else {
                        task.repeating = false;
                        sender.sendMessage("Last argument must be either \"true\" or \"false\". Set to false by default.");
                    }
                    plugin.tasks.add(task);
                    sender.sendMessage("Task created!");
                    sender.sendMessage(task.toString());
                    sender.sendMessage("Use \"/task init\" to add a command and start it.");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completion = new ArrayList<>();
        if (args.length == 1) {
            completion.add("create");
            completion.add("execute");
            completion.add("delete");
            completion.add("init");
            completion.add("list");
        } else if (args.length == 2) {
            String param = args[0];
            if (param.equals("execute") || param.equals("delete") || param.equals("init")) {
                for (Task task : plugin.tasks) {
                    completion.add(task.name);
                }
            }
        } else if (args.length == 3) {
            if (args[0].equals("create")) {
                String interval = args[2];
                boolean isNaN = true;
                try {
                    Double.parseDouble(interval);
                } catch (NumberFormatException e) {
                    isNaN = false;
                }
                if (interval.length() > 0 && isNaN) {
                    completion.add(interval + "t");
                    completion.add(interval + "s");
                    completion.add(interval + "m");
                    completion.add(interval + "h");
                }
            }
        } else if (args.length == 4) {
            if (args[0].equals("create")) {
                completion.add("true");
                completion.add("false");
            }
        }
        return completion;
    }
}
