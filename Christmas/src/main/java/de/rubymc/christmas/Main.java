package de.rubymc.christmas;

import de.rubymc.christmas.snow.StartSnow;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    static Main plugin;

    @Override
    public void onEnable() {
        new StartSnow();
    }

    public Main() {
        plugin = this;
    }

    public static Main getPlugin() {
        return plugin;
    }
}
