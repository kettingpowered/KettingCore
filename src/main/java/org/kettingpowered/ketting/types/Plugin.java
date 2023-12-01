package org.kettingpowered.ketting.types;

import java.io.File;
import java.util.function.BooleanSupplier;
import java.util.logging.Logger;

public record Plugin(
        PluginInfo info,
        BooleanSupplier enabled,
        File dataFolder,
        Logger logger
) {}
