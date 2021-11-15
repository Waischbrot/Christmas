package de.rubymc.christmas.utils;

import de.rubymc.christmas.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public class ConfigManager {
    
    FileConfiguration c = Main.getPlugin().getConfig();

    public ConfigManager() {
        this.load();
    }

    private void load() {
        if (!Main.getPlugin().getDataFolder().exists()) {
            Main.getPlugin().getDataFolder().mkdir();
        }

        if (!(new File(Main.getPlugin().getDataFolder(), "config.yml")).exists()) {
            Main.getPlugin().saveDefaultConfig();
        }
    }
    
    public int radius() {
        return c.getInt("snow.radius");
    }
    
    public int amount() {
        return c.getInt("snow.amount");
    }
    
    public List<String> worlds() {
        return c.getStringList("snow.worlds");
    }
    
    public List<String> biomes() {
        return c.getStringList("snow.biomes");
    }
    
    public boolean fixedspawn() {
        return c.getBoolean("snow.fixedspawn");
    }
}
