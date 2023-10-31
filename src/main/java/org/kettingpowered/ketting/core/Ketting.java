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

    public static final Logger LOGGER = LoggerFactory.getLogger(Ketting.class);
    private static final String coreVersion = Ketting.class.getPackage().getImplementationVersion();
    private static final Ketting INSTANCE = new Ketting();
    private static String mcVersion;

    private final Map<String, ForgeAdapter> AVAILABLE_ADAPTERS = new HashMap<>();

    public static Ketting init(String mcVersion) {
        if (Ketting.mcVersion != null)
            throw new RuntimeException("Ketting is already initialized");

        Ketting.mcVersion = mcVersion;
        return getInstance();
    }

    public static Ketting getInstance() {
        return INSTANCE;
    }

    public static String getCoreVersion() {
        return coreVersion;
    }

    public static String getMcVersion() {
        return mcVersion;
    }

    public Ketting() {
        LOGGER.info("KettingCore " + coreVersion + " for Minecraft " + mcVersion + " initializing...");
        collectAdapters();

        InjectProtect.init();
    }

    public @NotNull ForgeAdapter getAdapter() {
        ForgeAdapter adapter = AVAILABLE_ADAPTERS.get(mcVersion);

        if (adapter == null)
            throw new RuntimeException("Could not find an adapter for Minecraft version " + mcVersion);

        return adapter;
    }

    private void collectAdapters() {
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
}
