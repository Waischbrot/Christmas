package de.rubymc.christmas.snow;

import de.rubymc.christmas.Main;
import de.rubymc.christmas.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

public class SnowFlakes {

    private int version = Integer.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].split("_")[1]);
    private ConfigManager configManager;
    float radius;
    int amount;
    List<String> worlds;
    List<String> biomes;
    boolean fixedspawn;
    Random random;

    public SnowFlakes(ConfigManager configManager) {
        this.configManager = configManager;
        this.radius = (float) configManager.radius();
        this.amount = configManager.amount() / 4;
        this.worlds = configManager.worlds();
        this.biomes = configManager.biomes();
        this.fixedspawn = configManager.fixedspawn();
        this.schnee();
    }

    private void schnee() {
        this.random = new Random();
        (new BukkitRunnable() {
            public void run() {
                for (int i = 0; i < SnowFlakes.this.amount; ++i) {
                    float x = (SnowFlakes.this.random.nextFloat() - 0.5F) * SnowFlakes.this.radius * 2.0F;
                    float max = (float) Math.sqrt((double)(SnowFlakes.this.radius * SnowFlakes.this.radius - x * x)) * 2.0F;
                    float y = (SnowFlakes.this.random.nextFloat() - 0.5F) * max;
                    float z = (SnowFlakes.this.random.nextFloat() - 0.5F) * max;

                    Bukkit.getOnlinePlayers().forEach((player) -> {
                        Location playerLocation = player.getLocation();
                        if (SnowFlakes.this.worlds.contains(playerLocation.getWorld().getName()) && !SnowFlakes.this.biomes.contains(playerLocation.getBlock().getBiome().toString())) {
                            Location location = new Location(player.getWorld(), playerLocation.getX() + (double) x, playerLocation.getY() + (double) y, playerLocation.getZ() + (double) z);
                            if (!SnowFlakes.this.fixedspawn || (double) location.getWorld().getHighestBlockYAt(location) < location.getY()) {
                                try {
                                    if (13 <= SnowFlakes.this.version) {
                                        SnowFlakes.this.playerParticles(player, location.getX(), location.getY(), location.getZ(), 0.0D, 10);
                                    }
                                    else {
                                        SnowFlakes.this.playerParticles(player, "FIREWORKS_SPARK", (float) location.getX(), (float) location.getY(), (float) location.getZ(), new int[]{1});
                                    }
                                }
                                catch (SecurityException | NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException | ClassNotFoundException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        }).runTaskTimer(Main.getPlugin(), 0L, 2L);
    }

     private Class<?> getNMSClass(String nms) throws ClassNotFoundException {
         String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
         String name = "net.minecraft.server."  + version + nms;
         Class<?> nmsClass = Class.forName(name);
         return nmsClass;
     }

     private Class<?> getBukkitPlayerClass() throws ClassNotFoundException {
         String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
         String name = "org.bukkit.craftbukkit." + version + ".entity.CraftPlayer";
         Class<?> nmsClass = Class.forName(name.replace("/", "."));
         return nmsClass;
     }

     private Object getConnection(Player player) throws SecurityException, IllegalArgumentException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
         Method getHandle = player.getClass().getMethod("getHandle");
         Object nmsPlayer = getHandle.invoke(player);
         Field connection = nmsPlayer.getClass().getField("playerConnection");
         Object con = connection.get(nmsPlayer);
        return con;
     }

     public void playerParticles(Player player, double x, double y, double z, double data, int amount) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,  NoSuchFieldException {
        Class<?> bpClass = this.getBukkitPlayerClass();
        Class<?> particleClass = Class.forName("org.bukkit.Particle");
        Method valueOf = particleClass.getMethod("valueOf", String.class);
        Method spawnParticle = bpClass.getMethod("spawnParticle", particleClass, Double.TYPE, Double.TYPE, Double.TYPE, Integer.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE);
        spawnParticle.invoke(bpClass.cast(player), valueOf.invoke(particleClass, "FIREWORKS_SPARK"), x, y, z, amount, 0.0D, 0.0D, 0.0D, data);
     }

     public void playerParticles(Player player, String particle, float x, float y, float z, int[] amount) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,  NoSuchFieldException {
        Class<?> packetClass = this.getNMSClass("PacketPlayOutWorldParticles");
        Class<?> particleClass = this.getNMSClass("EnumParticle");
        Constructor<?> packetConstructor = packetClass.getConstructors()[1];
        Method valueOf = particleClass.getMethod("valueOf", String.class);
        Object packet = packetConstructor.newInstance(valueOf.invoke(particleClass, "FIREWORKS_SPARK"), true, x, y, z, 0.0F, 0.0F, 0.0F, 0.0F, 0 , amount);
        Method sendPacket = this.getNMSClass("PlayerConnection").getMethod("sendPacket", this.getNMSClass("Packet"));
        sendPacket.invoke(this.getConnection(player), packet);
     }
}


