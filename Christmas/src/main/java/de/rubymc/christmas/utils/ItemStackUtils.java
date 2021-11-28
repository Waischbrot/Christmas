package de.rubymc.christmas.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

public class ItemStackUtils {

    private static Method asNMSCopy;
    private static Method asBukkitCopy;
    private static Method getTag;
    private static Method setTag;
    private static Method getString;
    private static Method mojangsonParse;
    private static Class<?> craftItemStack;
    private static Class<?> nmsItemStack;
    private static Class<?> nbtTagCompound;
    private static Class<?> mojangsonParser;
    private static int versionId = Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].replace(".", "#").split("#")[1]);


    public static void loadUtils() {
        try {
            nbtTagCompound = Reflection.getNMSClass("NBTTagCompound");
            mojangsonParser = Reflection.getNMSClass("MojangsonParser");
            nmsItemStack = Reflection.getNMSClass("ItemStack");
            craftItemStack = Reflection.getClass("org.bukkit.craftbukkit." + Reflection.getVersion() + ".inventory.CraftItemStack");
            mojangsonParse = mojangsonParser.getDeclaredMethod("parse", String.class);
            asNMSCopy = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
            asBukkitCopy = craftItemStack.getDeclaredMethod("asBukkitCopy", nmsItemStack);
            getTag = nmsItemStack.getDeclaredMethod("getTag");
            setTag = nmsItemStack.getDeclaredMethod("setTag", nbtTagCompound);
            getString = nbtTagCompound.getDeclaredMethod("getString", String.class);
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }

    public static String getNBTString(ItemStack itemStack, String path) {
        String message = null;

        try {
            Object nmsCopy = asNMSCopy.invoke((Object)null, itemStack);
            Object tag = getTag.invoke(nmsCopy);
            String value = (String)getString.invoke(tag, path);
            if (!value.equals("")) {
                message = value;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return message;
    }

    public static String getNBTTags(ItemStack itemStack) {
        String message = "";

        try {
            Object nmsCopy = asNMSCopy.invoke((Object)null, itemStack);
            Object tag = getTag.invoke(nmsCopy);
            if (tag != null) {
                message = tag.toString();
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return message;
    }

    public static ItemStack applyNBTTags(ItemStack itemStack, String nbtTag) {
        if (nbtTag == null) {
            return itemStack;
        } else if (nbtTag.equals("")) {
            return itemStack;
        } else {
            ItemStack finalItemStack = itemStack;

            try {
                Object nmsCopy = asNMSCopy.invoke((Object)null, itemStack);
                Object nbtTagCompound = mojangsonParse.invoke((Object)null, nbtTag);
                setTag.invoke(nmsCopy, nbtTagCompound);
                finalItemStack = (ItemStack)asBukkitCopy.invoke((Object)null, nmsCopy);
            } catch (Exception var5) {
                var5.printStackTrace();
            }

            return finalItemStack;
        }
    }

    public static ItemStack getTexturedNBTItem(int amount, String displayName, String texture, String nbtTag) {
        ItemStack itemStack = getItem(Material.valueOf(versionId >= 13 ? "LEGACY_SKULL_ITEM" : "SKULL_ITEM"), amount, 3, displayName);
        itemStack = applyNBTTags(itemStack, nbtTag);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack = addTexture(itemStack, itemMeta, texture);
        itemMeta.setDisplayName(displayName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getTexturedItem(int amount, String displayName, String texture) {
        ItemStack itemStack = getItem(Material.valueOf(versionId >= 13 ? "LEGACY_SKULL_ITEM" : "SKULL_ITEM"), amount, 3, displayName);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack = addTexture(itemStack, itemMeta, texture);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack addTexture(ItemStack itemStack, ItemMeta meta, String texture) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), (String)null);
        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return itemStack;
    }

    public static String getTexture(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            GameProfile gameProfile = (GameProfile)profileField.get(meta);
            return gameProfile == null ? "" : ((Property)gameProfile.getProperties().get("textures").iterator().next()).getValue();
        } catch (Exception var4) {
            var4.printStackTrace();
            return "";
        }
    }

    public static ItemStack getItem(Material material, int amount, int data, String title, String... lores) {
        ItemStack itemStack = new ItemStack(material, amount, (short)((byte)data));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(Arrays.asList(lores));
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}