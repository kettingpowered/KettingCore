package org.kettingpowered.ketting.common.utils;

import java.util.Arrays;
import java.util.Collections;

public class DispatcherRedirector {

    public static String[] REDIRECTED = {
            "org.popcraft.chunky.ChunkyForge",
            "team.creative.playerrevive.PlayerRevive"
    };

    public static boolean needsRedirect() {
        StackWalker walker = StackWalker.getInstance(Collections.emptySet(), 10);
        boolean found = walker.walk(frames ->
                frames.map(StackWalker.StackFrame::getClassName)
                        .filter(IgnoredClasses::shouldCheck)
                        .anyMatch(name -> Arrays.stream(REDIRECTED).anyMatch(name::startsWith))
        );

        return found;
    }
}
