package org.kettingpowered.ketting.common.deobf;

import java.util.Map;

public record ClassMapping(
        String obfName,
        String deobfName,
        Map<String, String> methods
) {}
