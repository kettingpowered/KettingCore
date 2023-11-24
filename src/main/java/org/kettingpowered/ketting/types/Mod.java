package org.kettingpowered.ketting.types;

import java.util.List;

public record Mod(
        String modId,
        String description,
        String displayName,
        String version,
        boolean active,
        Side logicalSide,
        List<ModDependency> dependencies
) {

    public enum Side {
        CLIENT,
        SERVER,
        UNIVERSAL
    }
}
