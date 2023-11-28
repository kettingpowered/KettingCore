package org.kettingpowered.ketting.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kettingpowered.ketting.types.Plugin;

import java.io.File;
import java.util.List;

public interface BukkitAdapter extends SharedConstants {

    @Nullable Plugin getPlugin(String name);
    @Nullable List<Plugin> getPlugins();
    @Nullable String getPluginName(String name);
    @Nullable String getPluginName(@NotNull Plugin plugin);

    void loadPlugin(@NotNull File pluginFile) throws Exception;
    void loadPlugins(@NotNull File pluginDirectory) throws Exception;

    void enablePlugin(@NotNull Plugin plugin);
    void disablePlugin(@NotNull Plugin plugin);
    void disablePlugins();
}
