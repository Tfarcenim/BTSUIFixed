package io.karma.bts.server;

import io.karma.bts.common.CommonProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class ServerProxy extends CommonProxy {
    @Override
    public void onPreInit(final @NotNull FMLPreInitializationEvent event) {
        super.onPreInit(event);
    }

    @Override
    public void onInit(final @NotNull FMLInitializationEvent event) {
        super.onInit(event);
    }

    @Override
    public void onPostInit(final @NotNull FMLPostInitializationEvent event) {
        super.onPostInit(event);
    }
}
