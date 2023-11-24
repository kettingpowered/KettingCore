package org.kettingpowered.ketting.types;

public record ModDependency(
        String modId,
        String versionRange,
        boolean mandatory,
        Ordering loadOrder,
        Side logicalSide,
        Mod owner
) {
    public enum Ordering {
        BEFORE,
        AFTER,
        NONE
    }

    public enum Side {
        CLIENT,
        SERVER,
        UNIVERSAL
    }
}
