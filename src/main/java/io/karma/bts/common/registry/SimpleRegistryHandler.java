package io.karma.bts.common.registry;

import io.karma.bts.common.BTSConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.function.Supplier;

public final class SimpleRegistryHandler<E extends IForgeRegistryEntry<E>> extends AbstractRegistryHandler<E> {
    private final IForgeRegistry<E> registry;
    private final ArrayDeque<E> queue = new ArrayDeque<>();

    public SimpleRegistryHandler(final @NotNull IForgeRegistry<E> registry, final @NotNull RegistryStage stage) {
        super(stage);
        this.registry = registry;
    }

    public SimpleRegistryHandler(final @NotNull IForgeRegistry<E> registry) {
        this(registry, RegistryStage.PRE_INIT);
    }

    @Override
    public @NotNull Supplier<E> get(final @NotNull ResourceLocation name) {
        return () -> registry.getValue(name);
    }

    @Override
    public void register(@NotNull String name, @NotNull E entry) {
        entry.setRegistryName(new ResourceLocation(BTSConstants.MODID, name));
        queue.addLast(entry);
    }

    @Override
    protected void registerAll() {
        while (!queue.isEmpty()) {
            registry.register(queue.removeFirst());
        }
    }
}
