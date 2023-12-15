package io.karma.bts.common;

import io.karma.bts.common.network.*;
import io.karma.bts.common.registry.AutoRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
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
       // event.registerServerCommand(new BTSCommand());
    }

    private void registerPackets() {
        CHANNEL.registerMessage(ButtonCommandHandler.class, RunCommandPacket.class,4,Side.SERVER);
        CHANNEL.registerMessage(KeybindHandler.class, UseKeybindPacket.class,5,Side.SERVER);

        CHANNEL.registerMessage(C2SJumpPacketHandler.class, C2SJumpPacket.class,6,Side.SERVER);

    }
}
