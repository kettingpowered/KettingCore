package org.kettingpowered.ketting.adapter.noop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kettingpowered.ketting.adapter.ForgeAdapter;
import org.kettingpowered.ketting.types.Mod;

import java.util.List;

public class NOOPForgeAdapter implements ForgeAdapter {

    public static final NOOPForgeAdapter INSTANCE = new NOOPForgeAdapter();

    public String getMcVersion() {
        return null;
    }

    public void reload() {}

    public @Nullable Mod getMod(String modID) {
        return null;
    }

    public @Nullable List<Mod> getMods() {
        return null;
    }

    public @Nullable String getModName(String modID) {
        return null;
    }

    public @Nullable String getModName(@NotNull Mod mod) {
        return null;
    }
}
