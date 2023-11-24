package org.kettingpowered.ketting.types;

public record ModDependency(
        String modId,
        String versionRange,
        boolean mandatory,
        Ordering loadOrder,
        Mod.Side logicalSide,
        Mod owner
) {

    public enum Ordering {
        BEFORE,
        AFTER,
        NONE
    }
}
