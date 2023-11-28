package org.kettingpowered.ketting.types;

import java.io.File;
import java.util.function.Supplier;
import java.util.logging.Logger;

public record Plugin(
        PluginInfo info,
        Supplier<Boolean> enabled,
        File dataFolder,
        Logger logger
) {}
