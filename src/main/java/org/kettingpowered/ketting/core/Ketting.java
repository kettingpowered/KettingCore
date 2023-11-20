package org.kettingpowered.ketting.core;

import org.jetbrains.annotations.NotNull;
import org.kettingpowered.ketting.adapter.DimensionRegistry;
import org.kettingpowered.ketting.adapter.ForgeAdapter;
import org.kettingpowered.ketting.core.injectprotect.InjectProtect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class Ketting {

    public static final Logger LOGGER = LoggerFactory.getLogger(Ketting.class);
    private static final String coreVersion = Ketting.class.getPackage().getImplementationVersion();
    private static Ketting INSTANCE;
    private static String mcVersion;

    private final List<ForgeAdapter> AVAILABLE_ADAPTERS = new ArrayList<>();
    private final List<DimensionRegistry<?>> AVAILABLE_DIMENSION_REGISTRIES = new ArrayList<>();

    public static Ketting init(String mcVersion) {
        if (Ketting.mcVersion != null)
            throw new RuntimeException("Ketting is already initialized");

        Ketting.mcVersion = mcVersion;
        return getInstance();
    }

    public static Ketting getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Ketting();
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
        InjectProtect.init();
    }

    public void registerAdapter(ForgeAdapter adapter) {
        AVAILABLE_ADAPTERS.add(adapter);
    }

    public @NotNull ForgeAdapter getAdapter() {
        ForgeAdapter adapter = AVAILABLE_ADAPTERS.stream()
                .filter(adptr -> adptr.getMcVersion().equals(mcVersion))
                .findFirst().orElse(null);

        if (adapter == null)
            throw new RuntimeException("Could not find an adapter for Minecraft version " + mcVersion);

        return adapter;
    }

    public <T> void registerDimensionRegistry(DimensionRegistry<T> registry) {
        registry.createDefaults();
        AVAILABLE_DIMENSION_REGISTRIES.add(registry);
    }

    public @NotNull DimensionRegistry getDimensionRegistry() {
        DimensionRegistry registry = AVAILABLE_DIMENSION_REGISTRIES.stream()
                .filter(reg -> reg.getMcVersion().equals(mcVersion))
                .findFirst().orElse(null);

        if (registry == null)
            throw new RuntimeException("Could not find a dimension registry for Minecraft version " + mcVersion);

        return registry;
    }
}
