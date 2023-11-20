package io.karma.bts.client;

import io.karma.bts.client.input.BTSKeyBinds;
import io.karma.bts.client.render.HUDRenderer;
import io.karma.bts.client.render.PingRenderer;
import io.karma.bts.common.CommonProxy;
import io.karma.bts.common.util.PingColor;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(Side.CLIENT)
public final class ClientProxy {
    private static HUDRenderer hudRenderer;
    private static PingRenderer pingRenderer;

    @SubscribeEvent
    public static void setup(ModelRegistryEvent event) {
        final EventBus bus = MinecraftForge.EVENT_BUS;
        bus.register(ClientEventHandler.INSTANCE);
        bus.register(hudRenderer = new HUDRenderer());
        bus.register(pingRenderer = new PingRenderer());
        BTSKeyBinds.register();

    }

    public static void addPingToRenderer(@NotNull BlockPos pos, @NotNull EnumSet<PingColor> colors) {
        pingRenderer.addPing(pos, colors);
    }

    public static void removePingFromRenderer(@NotNull BlockPos pos, @NotNull EnumSet<PingColor> colors) {
        pingRenderer.removePing(pos, colors);
    }

    public static void clearPingRenderer() {
        pingRenderer.clearPings();
    }

    public static void requestHUDUpdate() {
        hudRenderer.readFromConfig();
    }
}
