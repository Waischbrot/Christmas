package de.rubymc.christmas.utils;

import de.rubymc.christmas.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class CalenderConfig {
    private Map<String, String> messages = new HashMap();
    private Map<Integer, ItemStack> inventoryDate = new HashMap();
    private Map<Integer, List<String>> calendarCommands = new HashMap();
    private Map<Integer, List<String>> calendarSounds = new HashMap();
    private Map<Integer, Boolean> calendarSoundEnabled = new HashMap();
    private Map<Integer, List<String>> calendarMessages = new HashMap();
    private Map<Integer, Boolean> calendarMessageEnabled = new HashMap();
    private String prefix;
    private ItemStack christmasCalendarItem;
    private Inventory inventory;
    private boolean useOlderDays;
    private static int versionId = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].replace(".", "#").split("#")[1]);

    public CalenderConfig(boolean reload) {
        Main.getPlugin().getConfig().options().copyDefaults(true);
        Main.getPlugin().getConfig().addDefault("messages.InventoryName", "        Christmas Calendar");
        Main.getPlugin().getConfig().addDefault("messages.Claimed", "%prefix%&7You claimed the &e%day%&7. Day!");
        Main.getPlugin().getConfig().addDefault("messages.AlreadyClaimed", "%prefix%&cYou already claimed this day!");
        Main.getPlugin().getConfig().addDefault("messages.NotClaimable", "%prefix%&cYou can't claim this day today!");
        Main.getPlugin().getConfig().addDefault("messages.NotDecember", "%prefix%&cIts not December!");
        Main.getPlugin().getConfig().addDefault("UseOlderDays", false);
        Main.getPlugin().getConfig().addDefault("Permission.OpenCalendar", "christmascalendar.open");

        int i;
        for(i = 1; i <= 24; ++i) {
            Main.getPlugin().getConfig().addDefault("CalendarDate." + i + ".Commands", Arrays.asList("give %player% minecraft:diamond " + i));
            Main.getPlugin().getConfig().addDefault("CalendarDate." + i + ".SoundEnabled", true);
            Main.getPlugin().getConfig().addDefault("CalendarDate." + i + ".Sounds", Arrays.asList("BLOCK_NOTE_BLOCK_PLING:1.0:1.0"));
            Main.getPlugin().getConfig().addDefault("CalendarDate." + i + ".MessageEnabled", true);
            Main.getPlugin().getConfig().addDefault("CalendarDate." + i + ".messages", Arrays.asList("%prefix%&7You received &e" + i + "x &bDiamond"));
        }

        if (!reload) {
            Main.getPlugin().saveConfig();
        }

        Main.getPlugin().reloadConfig();
        this.prefix = ChatColor.translateAlternateColorCodes('&', Main.getPlugin().getConfig().getString("messages.prefix"));
        this.useOlderDays = Main.getPlugin().getConfig().getBoolean("UseOlderDays");
        this.messages.put("InventoryName", ChatColor.translateAlternateColorCodes('&', Main.getPlugin().getConfig().getString("messages.InventoryName")));
        this.messages.put("Claimed", ChatColor.translateAlternateColorCodes('&', Main.getPlugin().getConfig().getString("messages.Claimed")));
        this.messages.put("AlreadyClaimed", ChatColor.translateAlternateColorCodes('&', Main.getPlugin().getConfig().getString("messages.AlreadyClaimed")));
        this.messages.put("NotClaimable", ChatColor.translateAlternateColorCodes('&', Main.getPlugin().getConfig().getString("messages.NotClaimable")));
        this.messages.put("NotDecember", ChatColor.translateAlternateColorCodes('&', Main.getPlugin().getConfig().getString("messages.NotDecember")));
        this.messages.put("OpenCalendarPermission", Main.getPlugin().getConfig().getString("Permission.OpenCalendar"));
        this.inventoryDate.put(10, ItemStackUtils.getTexturedItem(1, "§a1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I3ZTU4OTEzOGJiOGU3Y2FiYWJjMjNiNjJkMzEyYTM2OWNjMzRiN2Y0Y2E0MTU0ZDg3YjJkZTEwMWE4YzRkIn19fQ=="));
        this.inventoryDate.put(11, ItemStackUtils.getTexturedItem(1, "§a2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTM4N2YwNWY4NTM0MWNlM2NmNGIzNWUzYTNiMzljNGE2N2Q5NmEyMjBlMzk1NDM5YzM1ZjQ4YTM2OWQzNWZhIn19fQ=="));
        this.inventoryDate.put(12, ItemStackUtils.getTexturedItem(1, "§a3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZmRhNTk0NzQxOGY3OTE0NzVhMWE0ZGU0YTQ3ZDUzOWZhNmJkNWY0MjhhM2UzZDQ4Yjg2MjRhZDE1NTcifX19"));
        this.inventoryDate.put(13, ItemStackUtils.getTexturedItem(1, "§a4", "eyJ0aW1lc3RhbXAiOjE0OTkwMzMwMTQzMzYsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2VhOTQ5NmUxMWMzOGYyMTVjMGI2NzJiZTNkOGM4MTg3ZWI1NGIyZjIxMmZiODVmNTZmNTY2ZmE0YmYyMTNmMmYifX19"));
        this.inventoryDate.put(14, ItemStackUtils.getTexturedItem(1, "§a5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1ODVkNzg0MWQwNWY4OTM3NTYzMTU2NGZjYTc3NGIzZWRlZmU4NmFmMmI2YWUzOWYzYmFlY2ViYjU0OSJ9fX0="));
        this.inventoryDate.put(15, ItemStackUtils.getTexturedItem(1, "§a6", "eyJ0aW1lc3RhbXAiOjE0OTkwMzMwNjI4ODMsInByb2ZpbGVJZCI6ImRhNzQ2NWVkMjljYjRkZTA5MzRkOTIwMTc0NDkxMzU1IiwicHJvZmlsZU5hbWUiOiJJc2F5bGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ5OWJjZWI3NjY2YzI1MTA0MGFkYmJmOGYxNWJkMGUwODNmN2NlZDZlOTc0MTM2ODE4MjdmMzk3YmM4YiJ9fX0="));
        this.inventoryDate.put(16, ItemStackUtils.getTexturedItem(1, "§a7", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU1YzRlNTJkZTNhMzhjZDg3YmNmZjQ4NjY4YTU1Y2JiYWU1MDZjOWE5N2U1ZDhkZjZmNTk3NWU3NDQyNyJ9fX0="));
        this.inventoryDate.put(19, ItemStackUtils.getTexturedItem(1, "§a8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjEwMzM2NjdjZmYzYzdmZDkxZmM5OTU1MmE0MjgxZTc3ZjhhOWRiYTRjNzlkNzA0MWIyNDdkZDJjYThhMyJ9fX0="));
        this.inventoryDate.put(20, ItemStackUtils.getTexturedItem(1, "§a9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTU5YWRhMzE2MjNhODdmOGQ0YTBmOTkzNzBhYjYwMTAzZGJmZmYzNWE1OTE3ODk5OTg0ZTBhMGZiODlkZmZlIn19fQ=="));
        this.inventoryDate.put(21, ItemStackUtils.getTexturedItem(1, "§a10", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE5YTJiNWExOTZmZDBlNzIxZWU1MTlmZmM0YmVmNTFhMGFhY2I5OGExNDY0NmRkYmFjN2ZiNWI3ZjI3ZjIifX19"));
        this.inventoryDate.put(22, ItemStackUtils.getTexturedItem(1, "§a11", "eyJ0aW1lc3RhbXAiOjE0OTkwMzMxNjAzMjQsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU1NDM2Nzk3YzViNjcyY2EyMThhODY5ODgzM2Y0ZTkyN2U5MzNkYTRjM2ViMzM4YzM1ZGQ0MjUzOGU4ZmUifX19"));
        this.inventoryDate.put(23, ItemStackUtils.getTexturedItem(1, "§a12", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI5ZWQxYTQzYjMyNGY4NWFlNTlkN2ZhZmMzNGE5MTFjNWJhYmM1ZWRkZmZjOTZiZWJiYWNjMzVjYjlhZjc2ZSJ9fX0="));
        this.inventoryDate.put(24, ItemStackUtils.getTexturedItem(1, "§a13", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJkZDNlOGJlYjc4YzJhMzVlNmE5NmE0YzY3N2NjZmFlNDI2OTdiMTZhZmE4ZjUyYmU2OGI3YTQzMjRjNjZjIn19fQ=="));
        this.inventoryDate.put(25, ItemStackUtils.getTexturedItem(1, "§a14", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJjOTZiNTU3MDk3ZmVhOGQ1OGFhMWE2OGU2NGRjMTgxNjgwM2EyZjE5N2M0NzZiZWFlZDQxNDRlODVjOCJ9fX0="));
        this.inventoryDate.put(28, ItemStackUtils.getTexturedItem(1, "§a15", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM2YThhMzgwM2Q0OGI5ZTJkMjEyNTU5OTNhNTU4ZWYzZWY4YTc0NDEzMjExZjI0MjcxMWQ3MDI2YTczOGQ0In19fQ=="));
        this.inventoryDate.put(29, ItemStackUtils.getTexturedItem(1, "§a16", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI4MDdjNGQ5ZmMzZWNhZmI2Y2NkYjY1NjRjYTA3MzE4NDE0NGNkZTU2ZDU2NzEyNjdlNDY2NzdmNjY0NTcifX19"));
        this.inventoryDate.put(30, ItemStackUtils.getTexturedItem(1, "§a17", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQ1NzMxZDc2MDViODk1MzcyZDQxZjc3OTE0NTVhOTMzNTBhYjZmNzRkNTllNmQ4NTgyNjY4ZWJjYmE3In19fQ=="));
        this.inventoryDate.put(31, ItemStackUtils.getTexturedItem(1, "§a18", "eyJ0aW1lc3RhbXAiOjE0OTkwMzMxODM0NDgsInByb2ZpbGVJZCI6ImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwicHJvZmlsZU5hbWUiOiJNaW5pRGlnZ2VyVGVzdCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU4ODQzZTVjY2YyYzkxZmVmMTE0MTg1OTZjODU1ZTI1YTliYTZjZjE2NjY5Nzk2Y2RhNjY2ODliMTBkIn19fQ=="));
        this.inventoryDate.put(32, ItemStackUtils.getTexturedItem(1, "§a19", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcyY2FkMzc4NmZhNGM4M2ZmYWI5MTkyOWRmZGZjZGM1NjhlNDNkMjIxNzUxNjY1YTdlMzA5NDg5Mjk1MDU1In19fQ=="));
        this.inventoryDate.put(33, ItemStackUtils.getTexturedItem(1, "§a20", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWU1MjE5YWJlMmNmNTE4NGVlNWE1ZTk1MTEyN2E3ZjdkM2RlZTM5OWQ4YTQ2ODIyZjYzNzk0ZjE2NWUifX19"));
        this.inventoryDate.put(34, ItemStackUtils.getTexturedItem(1, "§a21", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWNhY2ZkYzc3OTUyYzdmMWFmYWNiYzVhYzY1Mzc2ZTQ5OGJkYTUzNmY0ZjYzOWZmZTYxNGQyODg5OGU0YTZhZCJ9fX0="));
        this.inventoryDate.put(37, ItemStackUtils.getTexturedItem(1, "§a22", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODA5MWI3ZmJkYzRmY2FlNWRmYTU5Yjc4NGUxMjJjZWI1YzJmZTUxYmFiYjVmZGNmZmE5ZGVhYmQ3MTA1In19fQ=="));
        this.inventoryDate.put(38, ItemStackUtils.getTexturedItem(1, "§a23", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTAxYjk5NmM1YWU1YTA1OGQxYTFkN2M1MzBjYzhmNWFjMTNlM2I0MWRlMjNkNjNkY2YzZWFmNzM2MGIzM2YifX19"));
        this.inventoryDate.put(39, ItemStackUtils.getTexturedItem(1, "§a24", "eyJ0aW1lc3RhbXAiOjE0OTkwMzMyMDg5MDAsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xZjYyYjgxYjRhMmRjNDRiZWZkZWRhYTMyNmFjY2EzOWFhNDJhMWMyNDg1ZTA0ZmE0MjRmYWU4ZmNhZWVlYjMifX19"));
        this.inventory = Bukkit.createInventory((InventoryHolder)null, 54, (String)this.messages.get("InventoryName"));

        for(i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventoryDate.containsKey(i)) {
                this.inventory.setItem(i, (ItemStack)this.inventoryDate.get(i));
            } else {
                this.inventory.setItem(i, ItemStackUtils.getItem(Material.valueOf(versionId >= 13 ? "LEGACY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"), 1, 7, " ", new String[0]));
            }
        }

        int[] inventorySlots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39};

        for(i = 1; i <= 24; ++i) {
            this.calendarCommands.put(inventorySlots[i - 1], Main.getPlugin().getConfig().getStringList("CalendarDate." + i + ".Commands"));
            this.calendarSoundEnabled.put(inventorySlots[i - 1], Main.getPlugin().getConfig().getBoolean("CalendarDate." + i + ".SoundEnabled"));
            this.calendarSounds.put(inventorySlots[i - 1], Main.getPlugin().getConfig().getStringList("CalendarDate." + i + ".Sounds"));
            this.calendarMessageEnabled.put(inventorySlots[i - 1], Main.getPlugin().getConfig().getBoolean("CalendarDate." + i + ".MessageEnabled"));
            this.calendarMessages.put(inventorySlots[i - 1], Main.getPlugin().getConfig().getStringList("CalendarDate." + i + ".messages"));
        }

    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isUseOlderDays() {
        return this.useOlderDays;
    }

    public String getMessage(String path) {
        return ((String)this.messages.get(path)).replace("%prefix%", this.prefix);
    }

    public void openChristmasCalendar(Player player) {
        if (!this.isDecember()) {
            player.sendMessage(this.getMessage("NotDecember"));
        } else {
            player.openInventory(this.inventory);
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public boolean isDecember() {
        return Calendar.getInstance().get(2) + 1 == 12;
    }

    public boolean isCurrentDay(int slotId) {
        Calendar calendar = Calendar.getInstance();
        int calendarDay = calendar.get(5);
        int day = Integer.valueOf(ChatColor.stripColor(this.inventory.getItem(slotId).getItemMeta().getDisplayName()));
        if (day == calendarDay) {
            return true;
        } else {
            return this.isUseOlderDays() && day <= calendarDay;
        }
    }

    public boolean isCorrectSlot(int slotId) {
        return this.inventoryDate.containsKey(slotId);
    }

    public boolean isClickable(int slotId) {
        if (this.isCorrectSlot(slotId)) {
            boolean december = this.isDecember();
            boolean currentDay = this.isCurrentDay(slotId);
            return december && currentDay;
        } else {
            return false;
        }
    }

    public boolean hasClicked(Player player, int slotId) {
        File file = new File(Main.getPlugin().getDataFolder(), "storedData/" + player.getUniqueId().toString() + ".yml");
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        fileConfiguration.options().copyDefaults(true);
        List<String> clickedSlots = fileConfiguration.getStringList("ClickedSlots");
        return clickedSlots.contains(String.valueOf(slotId));
    }

    public boolean markClicked(Player player, int slotId) {
        File file = new File(Main.getPlugin().getDataFolder(), "storedData/" + player.getUniqueId().toString() + ".yml");
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        fileConfiguration.options().copyDefaults(true);
        List<String> clickedSlots = fileConfiguration.getStringList("ClickedSlots");
        if (clickedSlots.contains(String.valueOf(slotId))) {
            return false;
        } else {
            clickedSlots.add(String.valueOf(slotId));
            fileConfiguration.set("ClickedSlots", clickedSlots);

            try {
                fileConfiguration.save(file);
                fileConfiguration.load(file);
            } catch (Exception var7) {
                var7.printStackTrace();
            }

            return true;
        }
    }

    public void playEffects(final Player player, int slotId) {
        Iterator var3 = ((List)this.calendarCommands.get(slotId)).iterator();

        String calendarSound;
        while(var3.hasNext()) {
            calendarSound = (String)var3.next();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), calendarSound.replace("%player%", player.getName()));
        }

        if ((Boolean)this.calendarMessageEnabled.get(slotId)) {
            var3 = ((List)this.calendarMessages.get(slotId)).iterator();

            while(var3.hasNext()) {
                calendarSound = (String)var3.next();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', calendarSound).replace("%prefix%", this.prefix).replace("%player%", player.getName()));
            }
        }

        if ((Boolean)this.calendarSoundEnabled.get(slotId)) {
            final List<String> sounds = (List)this.calendarSounds.get(slotId);
            if (sounds.size() <= 1) {
                calendarSound = (String)sounds.get(0);
                String sound = calendarSound.split(":")[0];
                String volume = calendarSound.split(":")[1];
                String pitch = calendarSound.split(":")[2];
                player.playSound(player.getEyeLocation(), Sound.valueOf(sound), Float.valueOf(volume), Float.valueOf(pitch));
            } else {
                (new BukkitRunnable() {
                    int id = 0;

                    public void run() {
                        if (sounds.isEmpty()) {
                            this.cancel();
                        } else {
                            String calendarSound = (String)sounds.get(this.id);
                            String sound = calendarSound.split(":")[0];
                            String volume = calendarSound.split(":")[1];
                            String pitch = calendarSound.split(":")[2];
                            player.playSound(player.getEyeLocation(), Sound.valueOf(sound), Float.valueOf(volume), Float.valueOf(pitch));
                            sounds.remove(calendarSound);
                        }
                    }
                }).runTaskTimer(Main.getPlugin(), 0L, 2L);
            }
        }

    }

    public void closeInventories() {
        for(int i = 0; i < this.inventory.getViewers().size(); ++i) {
            HumanEntity humanEntity = (HumanEntity)this.inventory.getViewers().get(i);
            humanEntity.closeInventory();
        }

    }
}