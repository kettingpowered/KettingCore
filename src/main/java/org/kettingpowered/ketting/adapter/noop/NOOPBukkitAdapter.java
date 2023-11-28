package org.kettingpowered.ketting.adapter.noop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kettingpowered.ketting.adapter.BukkitAdapter;
import org.kettingpowered.ketting.types.Plugin;

import java.io.File;
import java.util.List;

public class NOOPBukkitAdapter implements BukkitAdapter {

    public static final NOOPBukkitAdapter INSTANCE = new NOOPBukkitAdapter();

    public String getMcVersion() {
        return null;
    }

    public @Nullable Plugin getPlugin(String name) {
        return null;
    }

    public @Nullable List<Plugin> getPlugins() {
        return null;
    }

    public @Nullable String getPluginName(String name) {
        return null;
    }

    public @Nullable String getPluginName(@NotNull Plugin plugin) {
        return null;
    }

    public void loadPlugin(@NotNull File pluginFile) throws Exception {}

    public void loadPlugins(@NotNull File pluginDirectory) throws Exception {}

    public void enablePlugin(@NotNull Plugin plugin) {}

    public void disablePlugin(@NotNull Plugin plugin) {}

    public void disablePlugins() {}
}
