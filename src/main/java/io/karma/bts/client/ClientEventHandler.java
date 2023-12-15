package io.karma.bts.client;

import io.karma.bts.client.render.HUDRenderer;
import io.karma.bts.client.screen.HUDConfigScreen;
import io.karma.bts.client.screen.PauseScreen;
import io.karma.bts.common.BTSMod;
import io.karma.bts.common.network.C2SJumpPacket;
import io.karma.bts.common.network.C2SJumpPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
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
            AbstractClientPlayer clientPlayer = Minecraft.getMinecraft().player;
            if (clientPlayer != null) {
                if (clientPlayer.onGround) {
                    doubleJumped = false;
                    isHoldingFirstJump = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(final @NotNull RenderTickEvent event) {
        if (event.phase == Phase.START) {
            partialTicks = event.renderTickTime;
        }
    }

    public static boolean doubleJumped;

    public static boolean isHoldingFirstJump;

    @SubscribeEvent
    public void updateInputs(InputUpdateEvent event) {
        MovementInput movementInput = event.getMovementInput();
        boolean holdingJump = movementInput.jump;
        AbstractClientPlayer clientPlayer = Minecraft.getMinecraft().player;

        if (holdingJump && clientPlayer.onGround) {
            isHoldingFirstJump = true;
        }

        if (!holdingJump && !clientPlayer.onGround) {
            isHoldingFirstJump = false;
        }

        //duoble jump
        if (holdingJump && !clientPlayer.onGround && !isHoldingFirstJump) {
      //      if (!doubleJumped) {
      //          doubleJumped = true;

                BTSMod.CHANNEL.sendToServer(new C2SJumpPacket());

                //Minecraft.getMinecraft().player.jump();
       //     }
        }
    }

    @SubscribeEvent
    public void onChatGet(ClientChatReceivedEvent event) {
        ITextComponent message = event.getMessage();
        if (message instanceof TextComponentString) {
            TextComponentString textComponentString = (TextComponentString) message;
            String text = textComponentString.getText();
            int index = text.indexOf('[');
            if (index > -1) {
                String string = text.substring(index);
                int slash = string.indexOf("/");
                if (slash > -1) {
                    HUDRenderer.mana = Integer.parseInt(string.substring(1,slash));
                    HUDRenderer.maxMana = Integer.parseInt(string.substring(slash+1,string.indexOf(']')));



                    event.setCanceled(true);
                }
            }
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
