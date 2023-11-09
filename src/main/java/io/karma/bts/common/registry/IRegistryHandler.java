package io.karma.bts.common.registry;

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Base interface for all registry entry
 * handler implementations.
 * Register your handlers in {@link AutoRegistry}.
 *
 * @author KitsuneAlex
 * @since 23/06/2022
 */
public interface IRegistryHandler<E> {
    @NotNull Supplier<E> get(final @NotNull ResourceLocation name);

    void register(final @NotNull String name, final @NotNull E entry);

    default void onPreInit() {
    }

    default void onInit() {
    }
}
