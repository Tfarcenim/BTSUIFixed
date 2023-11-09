package io.karma.bts.common.registry;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractRegistryHandler<E> implements IRegistryHandler<E> {
    private final RegistryStage stage;

    protected AbstractRegistryHandler(final @NotNull RegistryStage stage) {
        this.stage = stage;
    }

    protected abstract void registerAll();

    @Override
    public void onPreInit() {
        if (stage == RegistryStage.PRE_INIT) {
            registerAll();
        }
    }

    @Override
    public void onInit() {
        if (stage == RegistryStage.INIT) {
            registerAll();
        }
    }
}
