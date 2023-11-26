package org.kettingpowered.ketting.common.utils;

import java.util.List;

public class IgnoredClasses {

    private static final List<String> DO_NOT_CHECK = List.of(
            "java.",
            "net.minecraft.",
            "net.minecraftforge.",
            "org.bukkit.",
            "org.kettingpowered.",
            "org.spigotmc.",
            "com.mojang.",
            "io.papermc.",
            "co.aikar.",
            "com.destroystokyo.",
            "jdk.internal.",
            "cpw.mods."
    );

    public static boolean shouldCheck(final String classpath) {
        for (final String clazz : DO_NOT_CHECK){
            if (classpath.startsWith(clazz))
                return false;
        }
        return true;
    }
}
