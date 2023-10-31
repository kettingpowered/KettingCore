package org.kettingpowered.adapters;

import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;
import org.kettingpowered.adapter.ForgeAdapter;

public class Forge_1_20_2_Adapter implements ForgeAdapter {

    public String getMcVersion() {
        return "1.20.2";
    }

    public @Nullable String getModName(String modID) {
        return ModList.get().getModContainerById(modID).map(modContainer -> modContainer.getModInfo().getDisplayName()).orElse(null);
    }
}
