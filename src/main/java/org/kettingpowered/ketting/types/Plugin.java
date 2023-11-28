package org.kettingpowered.ketting.types;

import java.io.File;
import java.util.logging.Logger;

public record Plugin(
        PluginInfo info,
        boolean enabled,
        File dataFolder,
        Logger logger
) {}
