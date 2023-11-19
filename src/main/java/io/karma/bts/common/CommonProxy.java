package io.karma.bts.common;

import io.karma.bts.common.command.BTSCommand;
import io.karma.bts.common.util.PingColor;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class CommonProxy {

    public CommonProxy() {
    }

    public void onPreInit(final @NotNull FMLPreInitializationEvent event) {

    }

    public void onInit(final @NotNull FMLInitializationEvent event) {
    }


    // Server stuff

    public void onServerStarting(final @NotNull FMLServerStartingEvent event) {
        event.registerServerCommand(new BTSCommand());
    }

    // Client stuff

    public void addPingToRenderer(@NotNull BlockPos pos, @NotNull EnumSet<PingColor> colors) {

    }

    public void removePingFromRenderer(@NotNull BlockPos pos, @NotNull EnumSet<PingColor> colors) {

    }

    public void clearPingRenderer() {
    }

    public void requestHUDUpdate() {
    }
}
