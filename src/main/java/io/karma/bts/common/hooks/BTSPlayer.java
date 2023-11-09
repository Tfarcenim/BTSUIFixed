package io.karma.bts.common.hooks;

import io.karma.bts.common.util.PingColor;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * Provides accessors for all data that is injected
 * into the player data.
 *
 * @author KitsuneAlex
 * @since 23/06/2022
 */
public interface BTSPlayer {
    @NotNull EnumSet<PingColor> getAssignedColors();

    void setAssignedColors(final @NotNull EnumSet<PingColor> colors);

    default boolean hasAssignedColors() {
        return !getAssignedColors().isEmpty();
    }
}
