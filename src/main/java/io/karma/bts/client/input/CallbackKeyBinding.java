package io.karma.bts.client.input;

import io.karma.bts.common.BTSConstants;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexander Hinze
 * @since 14/08/2022
 */
public final class CallbackKeyBinding extends KeyBinding {
    private static final String CATEGORY = String.format("key.%s.category", BTSConstants.MODID);

    private final Runnable callback;

    public CallbackKeyBinding(final @NotNull String name, final int key, final KeyModifier mod, final @NotNull Runnable callback) {
        super(String.format("key.%s.%s", BTSConstants.MODID, name), new ConflictContextImpl(), mod, key, CATEGORY);
        this.callback = callback;

        ClientRegistry.registerKeyBinding(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public CallbackKeyBinding(final @NotNull String name, final int key, final @NotNull Runnable callback) {
        this(name, key, KeyModifier.NONE, callback);
    }

    @SubscribeEvent
    public void onKeyInput(final @NotNull KeyInputEvent event) {
        if (!isPressed()) {
            return;
        }

        callback.run();
    }

    /**
     * @author Alexander Hinze
     * @since 14/08/2022
     */
    private static final class ConflictContextImpl implements IKeyConflictContext {
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public boolean conflicts(final @NotNull IKeyConflictContext other) {
            return false;
        }
    }
}
