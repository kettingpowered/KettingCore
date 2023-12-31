package org.kettingpowered.ketting.core.injectprotect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class InjectSet {

    private final IMixinInfo info;
    private final Throwable t;

    public InjectSet(IMixinInfo info, Throwable t) {
        this.info = info;
        this.t = t;
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull InjectSet of(IMixinInfo info, Throwable t) {
        return new InjectSet(info, t);
    }

    public @Nullable IMixinInfo getInfo() {
        return info;
    }

    public Throwable getThrowable() {
        return t;
    }
}
