package io.karma.bts.client;

import io.karma.bts.client.render.HUDRenderer;
import io.karma.bts.client.screen.HUDConfigScreen;
import io.karma.bts.client.screen.PauseScreen;
import io.karma.bts.common.BTSMod;
import io.karma.bts.common.network.C2SJumpPacket;
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

import java.util.List;

public final class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    private int ticks = 0;
    private float partialTicks = 0F;

    // @formatter:off
    private ClientEventHandler() {}
    // @formatter:on

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent event) {
        ITextComponent chat = event.getMessage();
        if (chat instanceof TextComponentString) {
            List<ITextComponent> siblings = chat.getSiblings();
            int size = siblings.size();
            if (size == 4 || size == 5) {
                ITextComponent manaText = siblings.get(0);
                if (manaText instanceof TextComponentString) {
                    TextComponentString manaTextCompoentString = (TextComponentString) manaText;
                    String rawS = manaTextCompoentString.getText();
                    if (rawS.startsWith("Mana:") || rawS.startsWith("Stamina:"))  {
                        TextComponentString manaNumbers = (TextComponentString)siblings.get(size - 1);
                        String raw = manaNumbers.getText();

                        String parsed = raw.substring(3);
                        parsed = parsed.substring(0,parsed.length()-1);

                        String[] strings = parsed.split("/");
                        HUDRenderer.mana = Integer.parseInt(strings[0]);
                        HUDRenderer.maxMana = Integer.parseInt(strings[1]);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

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


    public static boolean wasHoldingJump;
    static int jumpcount = 0;

    @SubscribeEvent
    public void updateInputs(InputUpdateEvent event) {
        MovementInput movementInput = event.getMovementInput();
        boolean holdingJump = movementInput.jump;
        AbstractClientPlayer clientPlayer = Minecraft.getMinecraft().player;

        if (clientPlayer.onGround) {
            jumpcount = 0;
        }

        if (clientPlayer.capabilities.isCreativeMode) return;

        if (!wasHoldingJump && holdingJump) {
            if (jumpcount > 0) {
             //   clientPlayer.motionY += 1;
                BTSMod.CHANNEL.sendToServer(new C2SJumpPacket());
            }
            jumpcount++;
        }
        wasHoldingJump = holdingJump;
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
