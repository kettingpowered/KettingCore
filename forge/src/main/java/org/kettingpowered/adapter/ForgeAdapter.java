package org.kettingpowered.adapter;

import org.jetbrains.annotations.Nullable;

public interface ForgeAdapter {

    String getMcVersion();

    @Nullable String getModName(String modID);
}
