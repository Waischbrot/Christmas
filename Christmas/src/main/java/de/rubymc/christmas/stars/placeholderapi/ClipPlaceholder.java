package de.rubymc.christmas.stars.placeholderapi;

import de.rubymc.christmas.stars.Starhandler;
import de.rubymc.christmas.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ClipPlaceholder extends PlaceholderExpansion{

    public void load() {
        this.register();
    }

    public boolean persist() {
        return true;
    }

    public boolean canRegister() {
        return true;
    }

    public String getIdentifier() {
        return "stars";
    }

    public String getAuthor() {
        return Main.getPlugin().getDescription().getAuthors().toString();
    }

    public String getVersion() {
        return Main.getPlugin().getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        else if (identifier.equalsIgnoreCase("starsfound")) {
            return String.valueOf(Starhandler.getStarsFound(player));
        }
        else {
            return identifier.equalsIgnoreCase("totalstars") ? String.valueOf(Starhandler.getTotalStars()) : null;
        }
    }
}
