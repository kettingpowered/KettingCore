package org.kettingpowered.ketting.core.injectprotect;

import org.jetbrains.annotations.NotNull;
import org.kettingpowered.ketting.common.betterui.BetterUI;
import org.kettingpowered.ketting.common.utils.ShortenedStackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.throwables.MixinError;

import java.util.ArrayList;
import java.util.List;

public class InjectProtect {

    private static final Logger LOGGER = LoggerFactory.getLogger(InjectProtect.class);
    private static final List<InjectSet> errors = new ArrayList<>();
    private static boolean shutdownCalled = false;

    public static void init() {
        LOGGER.info("Booting up InjectProtect");
        Mixins.registerErrorHandlerClass(MixinErrorHandler.class.getCanonicalName());
    }

    public static void onBootErrorCaught(MixinError error) {
        LOGGER.warn("Caught exception during server boot phase, shutting down server", ShortenedStackTrace.findCause(error));
        BetterUI.printError("Mixin related error", InjectionProcessor.getErroringMixin(error), new ShortenedStackTrace(error, 3));
        System.exit(1);
    }

    public static void mixinInjectCaught(IMixinInfo info, Throwable t) {
        LOGGER.warn("Caught mixin injection error!", ShortenedStackTrace.findCause(t));
        errors.add(InjectSet.of(info, t));
    }

    public static void shutdownCalled() {
        if (shutdownCalled)
            return;
        shutdownCalled = true;

        LOGGER.debug("Processing shutdown request");
        if (errors.isEmpty()) {
            LOGGER.debug("No errors found, shutting down");
            return;
        }

        if (errors.size() == 1) {
            LOGGER.debug("Found 1 error, showing user friendly error");
            Throwable t = errors.get(0).getThrowable();
            BetterUI.printError("Mixin injection error", InjectionProcessor.getErroringMixin(t) + getMod(t), new ShortenedStackTrace(t, 3));
            return;
        }

        LOGGER.debug("Found {} errors, showing user friendly error", errors.size());
        ShortenedStackTrace[] traces = new ShortenedStackTrace[errors.size()];
        String modIDS = "";
        for (int i = 0; i < errors.size(); i++) {
            Throwable t = errors.get(i).getThrowable();
            traces[i] = new ShortenedStackTrace(t, 3);
            modIDS += InjectionProcessor.getErroringMixin(t) + ", ";
        }

        modIDS = modIDS.substring(0, modIDS.length() - 2);

        BetterUI.printError("Mixin injection errors", "Multiple errors: " + modIDS, traces);
    }

    private static @NotNull String getMod(Throwable t) {
        String name = InjectionProcessor.getModName(t);
        return name == null ? "" : " (" + name + ")";
    }
}
