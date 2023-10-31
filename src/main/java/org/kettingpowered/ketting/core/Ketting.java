package org.kettingpowered.ketting.core;

import org.jetbrains.annotations.NotNull;
import org.kettingpowered.adapter.ForgeAdapter;
import org.kettingpowered.ketting.core.injectprotect.InjectProtect;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Ketting {

    public static Logger LOGGER = LoggerFactory.getLogger(Ketting.class);

    private static final Map<String, ForgeAdapter> AVAILABLE_ADAPTERS = new HashMap<>();

    private static String mcVersion;

    public static void init(String mcVersion) {
        Ketting.mcVersion = mcVersion;
        collectAdapters();

        InjectProtect.init();
    }

    private static void collectAdapters() {
        Reflections reflections = new Reflections("org.kettingpowered.adapters");

        reflections.getSubTypesOf(ForgeAdapter.class).forEach(clazz -> {
            try {
                ForgeAdapter adapter = clazz.getDeclaredConstructor().newInstance();
                AVAILABLE_ADAPTERS.put(adapter.getMcVersion(), adapter);
            } catch (Exception e) {
                LOGGER.error("Could not instantiate adapter " + clazz.getName(), e);
            }
        });
    }

    public static @NotNull ForgeAdapter getAdapter() {
        ForgeAdapter adapter = AVAILABLE_ADAPTERS.get(mcVersion);

        if (adapter == null)
            throw new RuntimeException("Could not find an adapter for Minecraft version " + mcVersion);

        return adapter;
    }
}
