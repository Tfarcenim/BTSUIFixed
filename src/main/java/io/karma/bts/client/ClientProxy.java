package io.karma.bts.client;

import io.karma.bts.client.input.BTSKeyBinds;
import io.karma.bts.client.render.HUDRenderer;
import io.karma.bts.client.render.PingRenderer;
import io.karma.bts.common.CommonProxy;
import io.karma.bts.common.util.PingColor;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

@SuppressWarnings("unused")
public final class ClientProxy extends CommonProxy {
    private HUDRenderer hudRenderer;
    private PingRenderer pingRenderer;

    @Override
    public void onPreInit(final @NotNull FMLPreInitializationEvent event) {
        super.onPreInit(event);

        final EventBus bus = MinecraftForge.EVENT_BUS;
        bus.register(ClientEventHandler.INSTANCE);
        bus.register(hudRenderer = new HUDRenderer());
        bus.register(pingRenderer = new PingRenderer());
    }

    @Override
    public void onInit(final @NotNull FMLInitializationEvent event) {
        super.onInit(event);
        BTSKeyBinds.register();
    }

    @Override
    public void onPostInit(final @NotNull FMLPostInitializationEvent event) {
        super.onPostInit(event);
    }

    @Override
    public void addPingToRenderer(@NotNull BlockPos pos, @NotNull EnumSet<PingColor> colors) {
        super.addPingToRenderer(pos, colors);
        pingRenderer.addPing(pos.toLong(), colors);
    }

    @Override
    public void removePingFromRenderer(@NotNull BlockPos pos, @NotNull EnumSet<PingColor> colors) {
        super.removePingFromRenderer(pos, colors);
        pingRenderer.removePing(pos.toLong(), colors);
    }

    @Override
    public void clearPingRenderer() {
        super.clearPingRenderer();
        pingRenderer.clearPings();
    }

    @Override
    public void requestHUDUpdate() {
        super.requestHUDUpdate();
        hudRenderer.readFromConfig();
    }
}
