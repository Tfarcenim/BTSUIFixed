package io.karma.bts.common.util;

import io.karma.bts.common.BTSConstants;
import org.jetbrains.annotations.NotNull;

public final class LangUtils {
    // @formatter:off
    private LangUtils() {}
    // @formatter:on

    public static @NotNull String key(final @NotNull String prefix, final @NotNull String name) {
        return String.format("%s.%s.%s", prefix, BTSConstants.MODID, name);
    }
}
