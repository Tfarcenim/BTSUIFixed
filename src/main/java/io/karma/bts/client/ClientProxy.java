package io.karma.bts.client;

import io.karma.bts.client.input.BTSKeyBinds;
import io.karma.bts.client.render.HUDRenderer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(Side.CLIENT)
public final class ClientProxy {
    private static HUDRenderer hudRenderer;

    @SubscribeEvent
    public static void setup(ModelRegistryEvent event) {
        final EventBus bus = MinecraftForge.EVENT_BUS;
        bus.register(ClientEventHandler.INSTANCE);
        bus.register(hudRenderer = new HUDRenderer());
        BTSKeyBinds.register();
    }
    public static void requestHUDUpdate() {
        hudRenderer.readFromConfig();
    }
}
