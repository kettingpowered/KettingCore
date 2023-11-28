package org.kettingpowered.ketting.types;

import java.util.List;
import java.util.Map;

public record PluginInfo(
        String name,
        List<String> provides,
        String main,
        String classLoaderOf,
        List<String> depends,
        List<String> softDepends,
        List<String> loadBefore,
        String version,
        Map<String, Map<String, Object>> commands,
        String description,
        List<String> authors,
        List<String> contributors,
        String website,
        String prefix,
        PluginLoadOrder order,
        String apiVersion,
        List<String> libraries
) {
    public enum PluginLoadOrder {
        STARTUP,
        POSTWORLD
    }
}
