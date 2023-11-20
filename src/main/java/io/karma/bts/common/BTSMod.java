package io.karma.bts.common;

import io.karma.bts.client.network.*;
import io.karma.bts.common.command.BTSCommand;
import io.karma.bts.common.registry.AutoRegistry;
import io.karma.bts.server.network.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Mod( // @formatter:off
    modid = BTSConstants.MODID,
    acceptedMinecraftVersions = BTSConstants.MC_VERSION,
   // acceptableRemoteVersions = BTSConstants.VERSION,
    dependencies = BTSConstants.DEPS
)
// @formatter:on
public final class BTSMod {
    public static final Logger LOGGER = LogManager.getLogger(BTSConstants.NAME);

    public static SimpleNetworkWrapper CHANNEL;

    @EventHandler
    public void onPreInit(final @NotNull FMLPreInitializationEvent event) {
        long startTime = System.nanoTime();
        long time = System.nanoTime() - startTime;
        LOGGER.info("Loaded configuration in {}ms ({}ns)", TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS), time);

        startTime = System.nanoTime();

        final EventBus bus = MinecraftForge.EVENT_BUS;
        bus.register(CommonEventHandler.INSTANCE);
        bus.register(PingHandler.INSTANCE);

        time = System.nanoTime() - startTime;
        LOGGER.info("Pre-initialization done in {}ms ({}ns)", TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS), time);

        AutoRegistry.INSTANCE.preInit(event.getAsmData());
    }

    @EventHandler
    public void onInit(final @NotNull FMLInitializationEvent event) {
        final long startTime = System.nanoTime();

        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(BTSConstants.MODID);
        registerPackets();
        AutoRegistry.INSTANCE.init();

        final long time = System.nanoTime() - startTime;
        LOGGER.info("Initialization done in {}ms ({}ns)", TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS), time);
    }

    @EventHandler
    public void onServerStarting(final @NotNull FMLServerStartingEvent event) {
        event.registerServerCommand(new BTSCommand());
    }

    private void registerPackets() {
        CHANNEL.registerMessage(HandlerAddPing.class, PacketAddPing.class, 1, Side.CLIENT);
        CHANNEL.registerMessage(HandlerRemovePing.class, PacketRemovePing.class, 2, Side.CLIENT);
        CHANNEL.registerMessage(HandlerClearPings.class, PacketClearPings.class, 3, Side.CLIENT);
        CHANNEL.registerMessage(ButtonCommandHandler.class, RunCommandPacket.class,4,Side.SERVER);
        CHANNEL.registerMessage(KeybindHandler.class, UseKeybindPacket.class,5,Side.SERVER);
    }
}
