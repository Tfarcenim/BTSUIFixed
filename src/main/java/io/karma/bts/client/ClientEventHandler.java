package io.karma.bts.client;

import io.karma.bts.client.screen.HUDConfigScreen;
import io.karma.bts.client.screen.PauseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.jetbrains.annotations.NotNull;

public final class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    private int ticks = 0;
    private float partialTicks = 0F;

    // @formatter:off
    private ClientEventHandler() {}
    // @formatter:on

    @SubscribeEvent
    public void onClientTick(final @NotNull ClientTickEvent event) {
        if (event.phase == Phase.END) {
            ticks++;
        }
    }

    @SubscribeEvent
    public void onRenderTick(final @NotNull RenderTickEvent event) {
        if (event.phase == Phase.START) {
            partialTicks = event.renderTickTime;
        }
    }

    @SubscribeEvent
    public void onGuiInit(final @NotNull GuiScreenEvent.InitGuiEvent event) {
        final GuiScreen screen = event.getGui();

        if (screen instanceof GuiIngameMenu) {
            FMLClientHandler.instance().showGuiScreen(new PauseScreen());
        }

        if (ClientConfig.enableHud && screen instanceof GuiOptions && Minecraft.getMinecraft().player != null) {
            final int buttonId = ClientConfig.hudButtonId;
            event.getButtonList().add(new GuiButton(buttonId, screen.width - 30, screen.height - 30, 20, 20, "H"));
        }
    }

    @SubscribeEvent
    public void onGuiActionPerformed(final @NotNull GuiScreenEvent.ActionPerformedEvent event) {
        final GuiScreen screen = event.getGui();

        if (ClientConfig.enableHud && screen instanceof GuiOptions && Minecraft.getMinecraft().player != null) {
            final int buttonId = ClientConfig.hudButtonId;

            if (event.getButton().id == buttonId) {
                FMLClientHandler.instance().showGuiScreen(new HUDConfigScreen());
            }
        }
    }

    public int getTicks() {
        return ticks;
    }

    public float getRenderTime() {
        return (float) ticks + partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
