package org.kettingpowered.ketting.types;

import java.util.UUID;
import java.util.function.BooleanSupplier;

public record Player(
        String name,
        UUID uuid,
        String ip,
        BooleanSupplier isOperator
) {}
