package de.rubymc.christmas.snow;

import de.rubymc.christmas.utils.ConfigManager;

public class StartSnow {
    
    public StartSnow() {
        new SnowFlakes(new ConfigManager());
    }
}

