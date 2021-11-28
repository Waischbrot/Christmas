package de.rubymc.christmas.stars;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.rubymc.christmas.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class Starhandler implements Listener {

    private static Map<Player, List<Starlocation>> unclaimedStars = new HashMap<>();
    private static List<Starlocation> availableStars = new ArrayList<>();

    private static int mcVersion;
    private static int versionID = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].replace(".", "#").split("#")[1]);

    private Map<String, Long> playerDelayTime = new HashMap<>();

    private static Class<?> tileEntityClass;
    private static Class<?> blockPositionClass;



    private static void loadPlayerStars(Player player) {
        if (unclaimedStars.containsKey(player)) {
            unclaimedStars.remove(player);
        }

        File playerFolder = new File(Main.getPlugin().getDataFolder(), "playerStars/" + player.getUniqueId().toString());
        ArrayList starlocations;
        if (!playerFolder.exists()) {
            starlocations = new ArrayList();
            starlocations.addAll(availableStars);
            unclaimedStars.put(player, starlocations);

            try {
                playerFolder.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        else {
            starlocations = new ArrayList();
            starlocations.addAll(availableStars);
            unclaimedStars.put(player, starlocations);

            FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(playerFolder);
            Iterator var = playerConfiguration.getStringList("StarLocations").iterator();

            while (var.hasNext()) {
                String star = (String) var.next();
                String world = star.split(":")[0];
                String x = star.split(":")[1];
                String y = star.split(":")[2];
                String z = star.split(":")[3];
                String command = star.replaceFirst(world + ":" + x + ":" + y + ":" + z + ":", "");
                World bukkitWorld = Bukkit.getWorld(world);
                if (bukkitWorld != null) {
                    Starlocation location = new Starlocation(bukkitWorld, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), command);
                    ((List) unclaimedStars.get(player)).remove(location);
                }
            }
        }
    }

    public static void loadStars() {
        availableStars = new ArrayList<>();

        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            mcVersion = Integer.parseInt(version.replaceAll("[^0-9]", ""));

            try {
                tileEntityClass = Class.forName("net.minecraft.server." + version + ".TileEntitySkull");
                if (mcVersion > 174) {
                    blockPositionClass = Class.forName("net.minecraft.server." + version + ".BlockPosition");
                }
                else {
                    blockPositionClass = null;
                }
            }
            catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        File playerFolders = new File(Main.getPlugin().getDataFolder(), "playerStars");
        if (!playerFolders.exists()) {
            playerFolders.mkdirs();
        }

        Iterator iterator = Main.getPlugin().getConfig().getStringList("StarLocations").iterator();

        while (iterator.hasNext()) {
            String star = (String) iterator.next();
            String world = star.split(":")[0];
            String x = star.split(":")[1];
            String y = star.split(":")[2];
            String z = star.split(":")[3];
            String command = star.replaceFirst(world + ":" + x + ":" + y + ":" + z + ":", "");
            World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld != null) {
                Starlocation starlocation = new Starlocation(bukkitWorld, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(z), command);
                availableStars.add(starlocation);
            }
        }

        iterator = Bukkit.getOnlinePlayers().iterator();

        while (iterator.hasNext()) {
            Player onlinePlayer = (Player) iterator.next();
            loadPlayerStars(onlinePlayer);
        }
    }

    public void unloadPlayerStars(Starlocation location) {
        File[] var = (new File(Main.getPlugin().getDataFolder(), "/playerStars")).listFiles();
        int i = var.length;

        for (int i1 = 0; i1 < i; ++i1) {
            File file = var[i1];
            FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(file);
            List<String> playerStars = playerConfiguration.getStringList("StarLocations");
            String path = location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + ":" + location.getCommand();
            if (playerStars.contains(path)) {
                playerStars.remove(path);
            }

            playerConfiguration.set("StarLocations", playerStars);

            try {
                playerConfiguration.save(file);
                playerConfiguration.load(file);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @EventHandler
    public void loadStars(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        loadPlayerStars(player);
    }

    @EventHandler
    public void unloadStars(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (unclaimedStars.containsKey(player)) {
            unclaimedStars.remove(player);
        }
    }

    @EventHandler
    public void unloadStars(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (unclaimedStars.containsKey(player)) {
            unclaimedStars.remove(player);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            boolean cancelled = false;

            if (versionID >= 9) {
                if (event.getHand() != null) {
                    if (!event.getHand().name().equals("HAND")) {
                        cancelled = true;
                    }
                }
                else {
                    cancelled = true;
                }
            }

            if (cancelled) {
                return;
            }

            Location location = event.getClickedBlock().getLocation();
            Starlocation starlocation = null;
            Iterator iterator = availableStars.iterator();

            while (iterator.hasNext()) {
                Starlocation star = (Starlocation) iterator.next();
                if (star.getBlock().getLocation().equals(location)) {
                    starlocation = star;
                }
            }

            if (starlocation == null) {
                return;
            }

            File playerFolder = new File(Main.getPlugin().getDataFolder(), "playerStars/" + player.getUniqueId().toString());
            FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(playerFolder);

            if (((List) unclaimedStars.get(player)).contains(starlocation)) {
                String commands = starlocation.getCommand().replace("%player%", player.getName()).replace("%totalstars%", String.valueOf(availableStars.size())).replace("%starsfound%", String.valueOf(playerConfiguration.getStringList("StarLocations").size() + 1));
                String[] var1 = commands.replace("|", "%#%").split("%#%");
                int var2 = var1.length;

                for (int var3 = 0; var3 < var2; ++var3) {
                    String command = var1[var3];
                    if (command.startsWith(" ")) {
                        command.replaceFirst(" ", "");
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }

                if (((List) unclaimedStars.get(player)).size() == 1 && Main.getPlugin().starsFinishEnabled()) {
                    Iterator finish = Main.getPlugin().starsFinishCommands().iterator();

                    while (finish.hasNext()) {
                        String command = (String) finish.next();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ChatColor.translateAlternateColorCodes('&', command.replace("%player%", player.getName())));
                    }
                }

                List<Starlocation> starlocationList = (List) unclaimedStars.get(player);
                starlocationList.remove(starlocation);
                unclaimedStars.put(player, starlocationList);
                this.playerDelayTime.put(player.getUniqueId().toString(), System.currentTimeMillis());
                Animation.playAnimation(starlocation);
                if (Main.getPlugin().foundMessageEnabled()) {
                    player.sendMessage(Main.getPlugin().foundMessage().replace("%totalstars%", String.valueOf(availableStars.size())).replace("%starsfound%", String.valueOf(playerConfiguration.getStringList("StarLocations").size() + 1)));
                }

                List<String> starLocations = playerConfiguration.getStringList("StarLocations");
                String world = starlocation.getWorld().getName();
                int x = starlocation.getBlock().getX();
                int y = starlocation.getBlock().getY();
                int z = starlocation.getBlock().getZ();
                starLocations.add(world + ":" + x + ":" + y + ":" + z + ":" + starlocation.getCommand());
                playerConfiguration.set("StarLocations", starLocations);

                try {
                    playerConfiguration.save(playerFolder);
                    playerConfiguration.load(playerFolder);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }

            }

            if (!((List) unclaimedStars.get(player)).contains(starlocation) && availableStars.contains(starlocation) && Main.getPlugin().foundMessageEnabled()) {
                if (this.playerDelayTime.containsKey(player.getUniqueId().toString())) {
                    long time = (Long) this.playerDelayTime.get(player.getUniqueId().toString());
                    if (System.currentTimeMillis() > time + 500L) {
                        player.sendMessage(Main.getPlugin().alreadyFound().replace("%totalstars%", String.valueOf(availableStars.size())).replace("%starsfound%", String.valueOf(playerConfiguration.getStringList("StarLocations").size())));
                        this.playerDelayTime.put(player.getUniqueId().toString(), System.currentTimeMillis());
                    }
                    else {
                        this.playerDelayTime.put(player.getUniqueId().toString(), System.currentTimeMillis());
                        player.sendMessage(Main.getPlugin().alreadyFound().replace("%totalstars%", String.valueOf(availableStars.size())).replace("%starsfound%", String.valueOf(playerConfiguration.getStringList("StarLocations").size())));
                    }
                }
            }
        }
    }

    @EventHandler
    public void placeStars(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission(Main.getPlugin().commandPerm()) && player.getItemInHand() != null && player.getItemInHand().getItemMeta() instanceof SkullMeta && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().getDisplayName().equals(Main.getPlugin().getPrefix() + " Erstelle Santas...")) {
            String command = ((String) player.getItemInHand().getItemMeta().getLore().get(0)).replaceFirst("§7Commands§8: ", "");
            command = ChatColor.stripColor(command);
            List<String> starLocations = Main.getPlugin().getConfig().getStringList("StarLocations");
            String world = event.getBlock().getWorld().getName();
            int x = event.getBlock().getX();
            int y = event.getBlock().getY();
            int z = event.getBlock().getZ();
            starLocations.add(world + ":" + x + ":" + y + ":" + z + ":" + command);
            Main.getPlugin().getConfig().set("StarLocations", starLocations);
            Main.getPlugin().saveConfig();
            Main.getPlugin().reloadConfig();
            availableStars.add(new Starlocation(event.getBlock().getWorld(), x, y, z, command));
            Iterator iterator = Bukkit.getOnlinePlayers().iterator();

            while (iterator.hasNext()) {
                Player onlinePlayer = (Player) iterator.next();
                loadPlayerStars(onlinePlayer);
            }

            this.skullTexture(RandomStar.getStar(), event.getBlock());
            player.sendMessage(Main.getPlugin().getPrefix() + "§7Du hast einen §cSanta §7hinzugefügt");
        }
    }

    @EventHandler
    public void breakStars(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission(Main.getPlugin().commandPerm())) {
            Location location = event.getBlock().getLocation();
            Starlocation starlocation = null;
            Iterator iterator = availableStars.iterator();

            while (iterator.hasNext()) {
                Starlocation star = (Starlocation) iterator.next();
                if (star.getBlock().getLocation().equals(location)) {
                    starlocation = star;
                }
            }

            if (starlocation == null) {
                return;
            }

            if (event.getBlock().getType() == Material.PLAYER_HEAD || starlocation != null && availableStars.contains(starlocation)) {
                availableStars.remove(starlocation);
                List<String> starLocations = Main.getPlugin().getConfig().getStringList("StarLocations");
                starLocations.remove(starlocation.getWorld().getName() + ":" + starlocation.getBlockX() + ":" + starlocation.getBlockY() + ":" + starlocation.getBlockZ() + ":" + starlocation.getCommand());
                Main.getPlugin().getConfig().set("StarLocations", starLocations);
                Main.getPlugin().saveConfig();
                Main.getPlugin().reloadConfig();
                this.unloadPlayerStars(starlocation);
                Iterator iterator1 = Bukkit.getOnlinePlayers().iterator();

                while (iterator1.hasNext()) {
                    Player onlinePlayer = (Player) iterator1.next();
                    loadPlayerStars(onlinePlayer);
                }

                player.sendMessage(Main.getPlugin().getPrefix() + "§7Du hast einen §cSanta §7entfernt.");
            }
        }
    }

    public void skullTexture(String texture, Block block) {
        Location location = block.getLocation();
        Starlocation starlocation = null;
        Iterator iterator = availableStars.iterator();

        while (iterator.hasNext()) {
            Starlocation star = (Starlocation) iterator.next();
            if (star.getBlock().getLocation().equals(location)) {
                starlocation = star;
            }
        }

        if (starlocation == null) {
            throw new IllegalArgumentException("Block muss ein Kopf sein!");
        }
        else {
            try {
                GameProfile profile = new GameProfile(UUID.randomUUID(), (String) null);
                profile.getProperties().put("textures", new Property("textures", texture));
                Object nmsWorld = block.getWorld().getClass().getMethod("getHandle").invoke(block.getWorld());
                Object tileEntity;
                Method getTileEntity;
                if (mcVersion <= 174) {
                    getTileEntity = nmsWorld.getClass().getMethod("getTileEntity", Integer.TYPE, Integer.TYPE, Integer.TYPE);
                    tileEntity = tileEntityClass.cast(getTileEntity.invoke(nmsWorld, block.getX(), block.getY(), block.getZ()));
                }
                else {
                    getTileEntity = nmsWorld.getClass().getMethod("getTileEntity", blockPositionClass);
                    tileEntity = tileEntityClass.cast(getTileEntity.invoke(nmsWorld, getBlockPosition(block.getX(), block.getY(), block.getZ())));
                }

                tileEntityClass.getMethod("setGameProfile", GameProfile.class).invoke(tileEntity, profile);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private static Object getBlockPosition(int x, int y, int z) {
        Object blockPosition = null;

        try {
            Constructor<?> cons = blockPositionClass.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
            blockPosition = cons.newInstance(x, y, z);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return blockPosition;
    }

    public static int getStarsFound(Player player) {
        int stars = -1;
        File playerFolder = new File(Main.getPlugin().getDataFolder(), "playerStars/" + player.getUniqueId().toString());
        if (playerFolder.exists()) {
            FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(playerFolder);
            stars = playerConfiguration.getStringList("StarLocations").size();
        }

        return stars;
    }

    public static int getTotalStars() {
        return availableStars.size();
    }
}
