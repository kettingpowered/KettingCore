package org.kettingpowered.ketting.core;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.kettingpowered.ketting.adapter.BukkitAdapter;
import org.kettingpowered.ketting.adapter.DimensionRegistry;
import org.kettingpowered.ketting.adapter.ForgeAdapter;
import org.kettingpowered.ketting.adapter.noop.NOOPBukkitAdapter;
import org.kettingpowered.ketting.adapter.noop.NOOPForgeAdapter;
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

    private final List<ForgeAdapter> AVAILABLE_FORGE_ADAPTERS = new ArrayList<>();
    private final List<BukkitAdapter> AVAILABLE_BUKKIT_ADAPTERS = new ArrayList<>();
    private final List<DimensionRegistry> AVAILABLE_DIMENSION_REGISTRIES = new ArrayList<>();

    /**
     * Should only be called by the server implementation
     */
    public static Ketting init(String mcVersion) {
        if (isInitialized())
            throw new RuntimeException("Ketting is already initialized");

        Ketting.mcVersion = mcVersion;
        return getInstance();
    }

    public static Ketting getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Ketting();
        return INSTANCE;
    }

    public static boolean isInitialized() {
        return mcVersion != null;
    }

    public static String getCoreVersion() {
        return coreVersion;
    }

    public static String getMcVersion() {
        return mcVersion;
    }

    private Ketting() {
        LOGGER.info("KettingCore " + coreVersion + " for Minecraft " + mcVersion + " initializing...");
        InjectProtect.init();
    }

    public void registerAdapter(ForgeAdapter adapter, BukkitAdapter bukkitAdapter) {
        AVAILABLE_FORGE_ADAPTERS.add(adapter);
        AVAILABLE_BUKKIT_ADAPTERS.add(bukkitAdapter);
    }

    public @NotNull ForgeAdapter getForgeAdapter() {
        ForgeAdapter adapter = AVAILABLE_FORGE_ADAPTERS.stream()
                .filter(adptr -> adptr.getMcVersion().equals(mcVersion))
                .findFirst().orElse(null);

        if (adapter == null) {
            LOGGER.error("Could not find a valid Forge adapter for Minecraft version " + mcVersion);
            return NOOPForgeAdapter.INSTANCE;
        }

        return adapter;
    }

    public @NotNull BukkitAdapter getBukkitAdapter() {
        BukkitAdapter adapter = AVAILABLE_BUKKIT_ADAPTERS.stream()
                .filter(adptr -> adptr.getMcVersion().equals(mcVersion))
                .findFirst().orElse(null);

        if (adapter == null) {
            LOGGER.error("Could not find a valid Bukkit adapter for Minecraft version " + mcVersion);
            return NOOPBukkitAdapter.INSTANCE;
        }

        return adapter;
    }

    public <T, K, V> void registerDimensionRegistry(DimensionRegistry<T, K, V> registry) {
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
