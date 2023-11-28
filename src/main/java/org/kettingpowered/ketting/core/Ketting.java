package org.kettingpowered.ketting.core;

import org.jetbrains.annotations.NotNull;
import org.kettingpowered.ketting.adapter.BukkitAdapter;
import org.kettingpowered.ketting.adapter.DimensionRegistry;
import org.kettingpowered.ketting.adapter.ForgeAdapter;
import org.kettingpowered.ketting.core.injectprotect.InjectProtect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Ketting {

    public static final Logger LOGGER = LoggerFactory.getLogger(Ketting.class);
    private static final String coreVersion = Ketting.class.getPackage().getImplementationVersion();
    private static Ketting INSTANCE;
    private static String mcVersion;

    private final List<ForgeAdapter> AVAILABLE_FORGE_ADAPTERS = new ArrayList<>();
    private final List<BukkitAdapter> AVAILABLE_BUKKIT_ADAPTERS = new ArrayList<>();
    private final List<DimensionRegistry> AVAILABLE_DIMENSION_REGISTRIES = new ArrayList<>();

    /**
     * <b>Should only be called by the server implementation</b>
     */
    public static Ketting init(@NotNull String mcVersion) {
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

    public void registerAdapter(@NotNull ForgeAdapter forgeAdapter, @NotNull BukkitAdapter bukkitAdapter) {
        registerForgeAdapter(forgeAdapter);
        registerBukkitAdapter(bukkitAdapter);
    }

    public void registerForgeAdapter(@NotNull ForgeAdapter adapter) {
        AVAILABLE_FORGE_ADAPTERS.add(adapter);
    }

    public void registerBukkitAdapter(@NotNull BukkitAdapter adapter) {
        AVAILABLE_BUKKIT_ADAPTERS.add(adapter);
    }

    public <T, K, V> void registerDimensionRegistry(DimensionRegistry<T, K, V> registry) {
        registry.createDefaults();
        AVAILABLE_DIMENSION_REGISTRIES.add(registry);
    }

    /**
     * Reloads all adapters, clearing all caches.
     * <br>
     * <b>Should only be called by the server implementation</b>
     */
    public void reload() {
        AVAILABLE_FORGE_ADAPTERS.forEach(ForgeAdapter::reload);
        AVAILABLE_BUKKIT_ADAPTERS.forEach(BukkitAdapter::reload);
    }

    /**
     * @return The first available Forge adapter for the current Minecraft version, or {@link Optional#empty()} if none is found.
     * @see #getBukkitAdapter()
     * @see #registerAdapter(ForgeAdapter, BukkitAdapter)
     */
    public @NotNull Optional<ForgeAdapter> getForgeAdapter() {
        Optional<ForgeAdapter> optional = AVAILABLE_FORGE_ADAPTERS.stream()
                .filter(adptr -> adptr.getMcVersion().equals(mcVersion))
                .findFirst();
        if (optional.isEmpty()) LOGGER.error("Could not find a valid Forge adapter for Minecraft version " + mcVersion);
        return optional;
    }

    /**
     * @return The first available Bukkit adapter for the current Minecraft version, or {@link Optional#empty()} if none is found.
     * @see #getForgeAdapter()
     * @see #registerAdapter(ForgeAdapter, BukkitAdapter)
     */
    public @NotNull Optional<BukkitAdapter> getBukkitAdapter() {
        Optional<BukkitAdapter> optional = AVAILABLE_BUKKIT_ADAPTERS.stream()
                .filter(adptr -> adptr.getMcVersion().equals(mcVersion))
                .findFirst();
        if (optional.isEmpty()) LOGGER.error("Could not find a valid Bukkit adapter for Minecraft version " + mcVersion);
        return optional;
    }

    /**
     * @return The first available dimension registry for the current Minecraft version, or {@link Optional#empty()} if none is found.
     * @see #registerDimensionRegistry(DimensionRegistry)
     */
    public @NotNull Optional<DimensionRegistry> getDimensionRegistry() {
        Optional<DimensionRegistry> optional = AVAILABLE_DIMENSION_REGISTRIES.stream()
                .filter(reg -> reg.getMcVersion().equals(mcVersion))
                .findFirst();
        if (optional.isEmpty()) LOGGER.error("Could not find a valid dimension registry for Minecraft version " + mcVersion);
        return optional;
    }
}
