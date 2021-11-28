package de.rubymc.christmas.calendar.commands;

import de.rubymc.christmas.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;

public class Commands implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kalender")) {
            if (sender.hasPermission(Main.getPlugin().commandPerm())) {
                if (args.length == 0) {
                    sender.sendMessage("§7/kalender open §8| §9Öffnet den Kalender!");
                    sender.sendMessage("§7/kalender reload §8| §9Läd die Konfiguration neu (:");
                }

                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage(Main.getPlugin().getConfiguration().getPrefix() + "§7Plugin wird neugeladen..");
                    Main.getPlugin().reload(true);
                    sender.sendMessage(Main.getPlugin().getConfiguration().getPrefix() + "§aProzess abgeschlossen.");
                }
            }

            if (sender instanceof Player) {
                Player player = (Player)sender;
                if (args.length == 1 && args[0].equalsIgnoreCase("open") && sender.hasPermission(Main.getPlugin().getConfiguration().getMessage("OpenCalendarPermission"))) {
                    Main.getPlugin().getConfiguration().openChristmasCalendar(player);
                }
            }
        }

        return false;
    }
}
