package de.rubymc.christmas.stars.commands;

import de.rubymc.christmas.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.util.*;

public class Commands implements CommandExecutor {

    private static int versionId = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].replace(".", "#").split("#")[1]);

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (!(commandSender instanceof Player)) {
            if (command.getName().equalsIgnoreCase("santa") && commandSender.hasPermission(Main.getPlugin().commandPerm())) {
                if (args.length == 0) {
                    commandSender.sendMessage(Main.getPlugin().getPrefix() + "§b/santa reload");
                }

                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    commandSender.sendMessage(Main.getPlugin().getPrefix() + "§7Plugin lädt neu...");
                    long startReloadDelay = System.currentTimeMillis();
                    Main.getPlugin().loadStarSettings(false);
                    long reloadDelay = System.currentTimeMillis() - startReloadDelay;
                    commandSender.sendMessage(Main.getPlugin().getPrefix() + "§7Plugin wurde in §7(§b" + reloadDelay + "ms§7) neugeladen!");
                }
            }

            return false;
        }
        else {
            Player player = (Player) commandSender;

            if (command.getName().equalsIgnoreCase("santa")) {
                if (player.hasPermission(Main.getPlugin().topPerm()) && !player.hasPermission(Main.getPlugin().commandPerm()) && args.length == 0) {
                    player.sendMessage(Main.getPlugin().getPrefix() + "§b/santa top");
                }

                if (player.hasPermission(Main.getPlugin().topPerm()) && args.length == 1 && args[0].equalsIgnoreCase("top")) {
                    Map<File, Integer> starValues = new HashMap<>();
                    Map<Integer, File> places = new HashMap<>();
                    int placeId = 1;
                    File[] files = (new File(Main.getPlugin().getDataFolder(), "/playerStars")).listFiles();
                    Arrays.sort(files, new Comparator<File>() {
                        @Override
                        public int compare(File f1, File f2) {
                            return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                        }
                    });
                    List<File> fileList = Arrays.asList(files);
                    Collections.reverse(fileList);
                    Iterator iterator = fileList.iterator();

                    File file;
                    while (iterator.hasNext()) {
                        file = (File) iterator.next();
                        FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(file);
                        int size = playerConfiguration.getStringList("StarLocations").size();
                        starValues.put(file, size);
                    }

                    int id = 1;

                    label1:
                    while (true) {
                        if (id > Main.getPlugin().topValue()) {
                            id = 1;

                            while (true) {
                                if (id > Main.getPlugin().topValue()) {
                                    break label1;
                                }

                                if (places.containsKey(id)) {
                                    String uuid = ((File) places.get(id)).getName();
                                    Player onlinePlayer = Bukkit.getPlayer(UUID.fromString(uuid));
                                    String playerName;
                                    if (onlinePlayer != null) {
                                        playerName = onlinePlayer.getName();
                                    }
                                    else {
                                        playerName = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                                    }

                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.getPlugin().topFormat().replace("%place%", String.valueOf(id)).replace("%player%", playerName).replace("%stars%", String.valueOf(YamlConfiguration.loadConfiguration((File) places.get(id)).getStringList("StarLocations").size()))));
                                }

                                ++id;
                            }
                        }

                        file = null;
                        Iterator iterator1 = starValues.entrySet().iterator();

                        while (iterator1.hasNext()) {
                            Map.Entry<File, Integer> entry = (Map.Entry) iterator1.next();
                            if (file == null) {
                                int maxValue = (Integer) Collections.max(starValues.values());
                                if ((Integer) entry.getValue() == maxValue) {
                                    file = (File) entry.getKey();
                                    places.put(placeId, entry.getKey());
                                    ++placeId;
                                }
                            }
                        }

                        starValues.remove(file);
                        ++id;
                    }
                }

                if (player.hasPermission(Main.getPlugin().commandPerm())) {
                    if (args.length == 0) {
                        player.sendMessage(Main.getPlugin().getPrefix() + "§b/santa top");
                        player.sendMessage(Main.getPlugin().getPrefix() + "§b/santa add <Command>");
                        player.sendMessage(Main.getPlugin().getPrefix() + "§b/santa reload");
                        player.sendMessage("");
                        player.sendMessage(Main.getPlugin().getPrefix() + "§7Du kannst §cSantas §7auch mit der Hand abbauen!");
                    }

                    if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                        player.sendMessage(Main.getPlugin().getPrefix() + "§7Plugin lädt neu...");
                        long startReloadDelay = System.currentTimeMillis();
                        Main.getPlugin().loadStarSettings(false);
                        long reloadDelay = System.currentTimeMillis() - startReloadDelay;
                        player.sendMessage(Main.getPlugin().getPrefix() + "§7Plugin wurde in §7(§b" + reloadDelay + "ms§7) neugeladen!");
                    }

                    if (args.length >= 2 && args[0].equalsIgnoreCase("add")) {
                        StringBuilder builder = new StringBuilder();

                        for (int i = 1; i < args.length; ++i) {
                            builder.append(args[i] + " ");
                        }

                        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta)itemStack.getItemMeta();
                        meta.setDisplayName(Main.getPlugin().getPrefix() + " Erstelle Santas...");
                        meta.setLore(Arrays.asList("§7Commands§8: " + builder.toString()));
                        itemStack.setItemMeta(meta);
                        HashMap<Integer, ItemStack> droppedItems = player.getInventory().addItem(new ItemStack[]{itemStack});
                        if (droppedItems.isEmpty()) {
                            player.sendMessage(Main.getPlugin().getPrefix() + "§7Ein Item um §cSantas §7zu erstellen, wurde in dein Inventar gelegt!");
                        } else {
                            player.sendMessage(Main.getPlugin().getPrefix() + "§7Dein Inventar ist zu voll, um einen §cSanta §7hineinzulegen!");
                        }
                    }
                }
            }

            return false;
        }
    }
}
