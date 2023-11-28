package org.kettingpowered.ketting.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kettingpowered.ketting.types.Mod;

import java.util.List;

public interface ForgeAdapter extends SharedConstants {

    @Nullable Mod getMod(String modID);
    @Nullable List<Mod> getMods();
    @Nullable String getModName(String modID);
    @Nullable String getModName(@NotNull Mod mod);
}
