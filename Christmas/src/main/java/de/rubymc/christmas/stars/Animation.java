package de.rubymc.christmas.stars;

import de.rubymc.christmas.Main;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class Animation {

    public static void playAnimation(Location location) {

        if (Main.getPlugin().getEnabledAnimations().contains("Firework")) {
            final Firework firework = (Firework) location.getWorld().spawn(location.clone().add(0.5D, 0.0D, 0.5D), Firework.class);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            Type fireworkType = Type.values()[ThreadLocalRandom.current().nextInt(Type.values().length)];
            Color fireworkColor = Color.fromRGB(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255));
            Color fireworkFadeColor = Color.fromRGB(ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255), ThreadLocalRandom.current().nextInt(255));
            fireworkMeta.addEffect(FireworkEffect.builder().flicker(ThreadLocalRandom.current().nextBoolean()).trail(ThreadLocalRandom.current().nextBoolean()).with(fireworkType).withFade(fireworkFadeColor).withColor(fireworkColor).build());
            fireworkMeta.setPower(1);
            firework.setFireworkMeta(fireworkMeta);
            (new BukkitRunnable() {
                public void run() {
                    firework.detonate();
                }
            }).runTaskLater(Main.getPlugin(), 5L);
        }
    }
}
