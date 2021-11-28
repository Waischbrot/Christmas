package de.rubymc.christmas;

import de.rubymc.christmas.calendar.ChristmasCalendarHandler;
import de.rubymc.christmas.snow.StartSnow;
import de.rubymc.christmas.stars.Starhandler;
import de.rubymc.christmas.stars.commands.Commands;
import de.rubymc.christmas.utils.CalenderConfig;
import de.rubymc.christmas.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    FileConfiguration c = this.getConfig();
    public static Main plugin;
    private List<String> enabledAnimations;
    private String prefix;
    private Boolean starsFinishEnabled;
    private List<String> starsFinishCommands;
    private int topValue;
    private String topFormat;
    private Boolean animationEnabled;
    private Boolean foundMessageEnabled;
    private String foundMessage;
    private String alreadyfoundMessage;
    private String commandPerm;
    private String topPerm;

    private CalenderConfig configuration;

    @Override
    public void onEnable() {
        plugin = this;
        new StartSnow();

        ItemStackUtils.loadUtils();
        Bukkit.getPluginManager().registerEvents(new ChristmasCalendarHandler(), this);
        this.reload(false);
        this.getCommand("kalender").setExecutor(new de.rubymc.christmas.calendar.commands.Commands());

        this.getCommand("santa").setExecutor(new Commands());
        Bukkit.getPluginManager().registerEvents(new Starhandler(), this);

        this.loadStarSettings(true);
    }

    public static Main getPlugin() {
        return plugin;
    }

    public void loadStarSettings(boolean start) {
        this.enabledAnimations = new ArrayList();
        this.getConfig().options().copyDefaults(true);
        if (start) {
            this.saveConfig();
        }

        this.reloadConfig();
        if (this.getConfig().getBoolean("stars.animation.enabled")) {
            this.enabledAnimations.add("Firework");
        }

        this.prefix = ChatColor.translateAlternateColorCodes('&', c.getString("messages.prefix"));
        this.starsFinishEnabled = c.getBoolean("stars.finish.enabled");
        this.starsFinishCommands = c.getStringList("stars.finish.commands");
        this.topValue = c.getInt("stars.top.value");
        this.topFormat = c.getString("stars.top.format");
        this.animationEnabled = c.getBoolean("stars.animation.enabled");
        this.foundMessageEnabled = c.getBoolean("stars.foundmessage.enabled");
        this.foundMessage = ChatColor.translateAlternateColorCodes('&', c.getString("stars.foundmessage.found"));
        this.alreadyfoundMessage = ChatColor.translateAlternateColorCodes('&', c.getString("stars.foundmessage.alreadyfound"));
        this.commandPerm = c.getString("permission.command");
        this.topPerm = c.getString("permission.topcommand");
        int delay = this.getConfig().getInt("stars.delay");

        if (delay == 0) {
            Starhandler.loadStars();
        } else {
            (new BukkitRunnable() {
                public void run() {
                    Starhandler.loadStars();
                }
            }).runTaskLater(getPlugin(), (long)(delay * 20));
        }

    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean starsFinishEnabled() {
        return this.starsFinishEnabled;
    }

    public List<String> starsFinishCommands() {
        return this.starsFinishCommands;
    }

    public int topValue() {
        return this.topValue;
    }

    public String topFormat() {
        return this.topFormat.replace("%prefix%", this.getPrefix());
    }

    public boolean animationEnabled() {
        return this.animationEnabled;
    }

    public boolean foundMessageEnabled() {
        return this.foundMessageEnabled;
    }

    public String foundMessage() {
        return this.foundMessage.replace("%prefix%", this.getPrefix());
    }

    public String alreadyFound() {
        return this.alreadyfoundMessage.replace("%prefix%", this.getPrefix());
    }

    public String commandPerm() {
        return this.commandPerm;
    }

    public String topPerm() {
        return this.topPerm;
    }

    public List<String> getEnabledAnimations() {
        return this.enabledAnimations;
    }

    public CalenderConfig getConfiguration() {
        return this.configuration;
    }

    public void reload(boolean reload) {
        this.configuration = new CalenderConfig(reload);
    }

    @Override
    public void onDisable() {
        this.configuration.closeInventories();
    }

}


