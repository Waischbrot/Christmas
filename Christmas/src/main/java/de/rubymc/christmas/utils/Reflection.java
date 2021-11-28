package de.rubymc.christmas.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

    public static Class<?> getNMSClass(String name) {
        return getClass("net.minecraft.server." + getVersion() + "." + name);
    }

    public static Class<?> getCraftClass(String name) {
        return getClass("org.bukkit.craftbukkit." + getVersion() + "." + name);
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    public static Object getValue(Object o, String fieldName) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            return field.get(o);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Object o, String methodName, Class<?>... params) {
        try {
            Method method = o.getClass().getMethod(methodName, params);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            return method;
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static Field getField(Field field) {
        field.setAccessible(true);
        return field;
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }
}