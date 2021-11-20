package de.rubymc.christmas.stars;

import org.bukkit.World;
import org.bukkit.Location;

public class Starlocation extends Location{

    private String command;

    public Starlocation(World world, int x, int y, int z, String command) {
        super(world, (double)x, (double)y, (double)z);
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

}
