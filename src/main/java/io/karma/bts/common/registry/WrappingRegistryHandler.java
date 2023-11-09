package io.karma.bts.common.registry;

import io.karma.bts.common.BTSConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.function.Function;
import java.util.function.Supplier;

public final class WrappingRegistryHandler<E, W extends IForgeRegistryEntry<W>> extends AbstractRegistryHandler<E> {
    private final IForgeRegistry<W> registry;
    private final Function<E, W> wrapper;
    private final Function<W, E> unwrapper;
    private final ArrayDeque<W> queue = new ArrayDeque<>();

    // @formatter:off
    public WrappingRegistryHandler(final @NotNull IForgeRegistry<W> registry,
                                   final @NotNull RegistryStage stage,
                                   final @NotNull Function<E, W> wrapper,
                                   final @NotNull Function<W, E> unwrapper
    ) { // @formatter:on
        super(stage);
        this.registry = registry;
        this.wrapper = wrapper;
        this.unwrapper = unwrapper;
    }

    // @formatter:off
    public WrappingRegistryHandler(final @NotNull IForgeRegistry<W> registry,
                                   final @NotNull Function<E, W> wrapper,
                                   final @NotNull Function<W, E> unwrapper
    ) { // @formatter:on
        this(registry, RegistryStage.PRE_INIT, wrapper, unwrapper);
    }

    @Override
    public @NotNull Supplier<E> get(final @NotNull ResourceLocation name) {
        return () -> unwrapper.apply(registry.getValue(name));
    }

    @Override
    public void register(final @NotNull String name, final @NotNull E entry) {
        final W wrappedEntry = wrapper.apply(entry);
        wrappedEntry.setRegistryName(new ResourceLocation(BTSConstants.MODID, name));
        queue.addLast(wrappedEntry);
    }

    @Override
    protected void registerAll() {
        while (!queue.isEmpty()) {
            registry.register(queue.removeFirst());
        }
    }
}
