package io.karma.bts.client.screen;

import io.karma.bts.common.BTSConstants;
import io.karma.bts.common.BTSMod;
import io.karma.bts.common.util.ColorUtils;
import io.karma.bts.Easings;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class PauseScreen extends BTSScreen {
    private static final ResourceLocation texture = new ResourceLocation(BTSConstants.MODID, "textures/gui/gui_elements.png");

    private static final int brandingWidth = 212 >> 2;
    private static final int brandingHeight = 118 >> 2;
    private static final int brandingU = 316 >> 2;
    private static final int brandingV = 0;

    private static final int decorationWidth = 412 >> 2;
    private static final int decorationHeight = 512 >> 2;
    private static final int decorationU = 0;
    private static final int decorationV = 460 >> 2;
    private static final float decorationSpeed = 0.025F;

    private static final float buttonSpeed = 0.05F;
    private static final int buttonOffsetStep = 15;
    private static final float buttonBaseScale = 1.125F;
    private static final float buttonSelectionIncStep = 0.25F;
    private static final float buttonSelectionDecStep = 0.5F;
    private static final float buttonSelectionScaleFactor = 0.025F;

    private static final int resumeButtonWidth = 314 >> 2;
    private static final int resumeButtonHeight = 126 >> 2;
    private static final int resumeButtonU = 0;
    private static final int resumeButtonV = 0;

    private static final int optionsButtonWidth = 274 >> 2;
    private static final int optionsButtonHeight = 111 >> 2;
    private static final int optionsButtonU = 0;
    private static final int optionsButtonV = 128 >> 2;

    private static final int discordButtonWidth = 274 >> 2;
    private static final int discordButtonHeight = 111 >> 2;
    private static final int discordButtonU = 276 >> 2;
    private static final int discordButtonV = 120 >> 2;

    private static final int websiteButtonWidth = 274 >> 2;
    private static final int websiteButtonHeight = 111 >> 2;
    private static final int websiteButtonU = 0;
    private static final int websiteButtonV = 238 >> 2;

    private static final int quitButtonWidth = 274 >> 2;
    private static final int quitButtonHeight = 111 >> 2;
    private static final int quitButtonU = 0;
    private static final int quitButtonV = 349 >> 2;

    private final float[] buttonTimers = new float[5];
    private final float[] buttonOffsets = new float[5];
    private final float[] prevButtonOffsets = new float[5];
    private final boolean[] isMouseOverButton = new boolean[5];
    private final float[] buttonMouseOverTimers = new float[5];
    private final float[] buttonMouseOverDeltas = new float[5];
    private final float[] prevButtonMouseOverDelta = new float[5];
    private float currentButtonScale = 0F;
    private int currentButtonGap = 0;

    private float decorationTimer = 0F;
    private float decorationAlpha = 0F;
    private float prevDecorationAlpha = 0F;

    @Override
    public void drawScreen(final int mx, final int my, final float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mx, my, partialTicks);

        final TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(texture);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        drawDecoration(partialTicks);
        drawBranding(partialTicks);

        final int buttonHeight = height / 5;
        currentButtonScale = buttonBaseScale + (1F / (float) height) * (float) buttonHeight;
        currentButtonGap = (int) ((float) (buttonHeight >> 2) * buttonBaseScale);

        drawResumeButton(mx, my, partialTicks);
        drawOptionsButton(mx, my, partialTicks);
        drawDiscordButton(mx, my, partialTicks);
        drawWebsiteButton(mx, my, partialTicks);
        drawQuitButton(mx, my, partialTicks);

        GlStateManager.disableBlend();
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        final SoundHandler soundHandler = mc.getSoundHandler();

        if (isMouseOverButton[0]) {
            FMLClientHandler.instance().showGuiScreen(null);
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
        }
        else if (isMouseOverButton[1]) {
            FMLClientHandler.instance().showGuiScreen(new GuiOptions(this, mc.gameSettings));
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
        }
        else if (isMouseOverButton[2]) {
            if (!Desktop.isDesktopSupported()) {
                BTSMod.LOGGER.error("Desktop not supported");
                return;
            }

            try {
                Desktop.getDesktop().browse(new URI("https://discord.gg/pVA4NZxTMY"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
        }
        else if (isMouseOverButton[3]) {
            if (!Desktop.isDesktopSupported()) {
                BTSMod.LOGGER.error("Desktop not supported");
                return;
            }
            try {
                Desktop.getDesktop().browse(new URI("https://beyondtheseas.net"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
        }
        else if (isMouseOverButton[4]) {
            soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
            mc.world.sendQuittingDisconnectingPacket();
            mc.loadWorld(null);

            if (mc.isConnectedToRealms()) {
                new RealmsBridge().switchToRealms(new GuiMainMenu());
            }
            else {
                FMLClientHandler.instance().showGuiScreen(new GuiMainMenu());
            }
        }
    }

    private void drawResumeButton(final int mx, final int my, final float partialTicks) {
        prevButtonMouseOverDelta[0] = buttonMouseOverDeltas[0];
        buttonMouseOverDeltas[0] = Easings.easeInSine(buttonMouseOverTimers[0]);
        final float d = prevButtonMouseOverDelta[0] + (buttonMouseOverDeltas[0] - prevButtonMouseOverDelta[0]) * partialTicks;
        final float s = buttonSelectionScaleFactor * d;

        final float o = buttonTimers[0] < 1F ? prevButtonOffsets[0] + (buttonOffsets[0] - prevButtonOffsets[0]) * partialTicks : 1F;
        final float xo = (float) width - ((width >> 1) * o) + buttonOffsetStep;
        final float yo = (height >> 1) - (currentButtonGap + (currentButtonGap << 1) + optionsButtonHeight + discordButtonHeight);

        GlStateManager.pushMatrix();
        GlStateManager.translate(xo, yo, zLevel + 2F);
        GlStateManager.scale(currentButtonScale + s, currentButtonScale + s, 1F);

        final int color = ColorUtils.lerpRGBA(0xAAAAAAFF, 0xFFFFFFFF, d);
        final int[] channels = new int[4];
        ColorUtils.unpackRGBA(color, channels);
        final float c = (float) channels[0] / 255F;

        GlStateManager.color(c, c, c, 1F);

        GuiUtils.drawTexturedModalRect(0, 0, resumeButtonU, resumeButtonV, resumeButtonWidth, resumeButtonHeight, 0F);
        GlStateManager.popMatrix();

        if (buttonTimers[0] < 1F) {
            buttonTimers[0] += buttonSpeed * partialTicks;
            prevButtonOffsets[0] = buttonOffsets[0];
            buttonOffsets[0] = Easings.easeOutElastic(buttonTimers[0]);
        }
        else {
            final int actualWidth = (int) (resumeButtonWidth * currentButtonScale);
            final int actualHeight = (int) (resumeButtonHeight * currentButtonScale);

            final int minX = (int) xo;
            final int minY = (int) yo;
            final int maxX = minX + actualWidth;
            final int maxY = minY + actualHeight;
            isMouseOverButton[0] = (mx >= minX && mx <= maxX) && (my >= minY && my <= maxY);
        }

        if (isMouseOverButton[0]) {
            if (buttonMouseOverTimers[0] < 1F) {
                buttonMouseOverTimers[0] = Math.min(1F, buttonMouseOverTimers[0] + (buttonSelectionIncStep * partialTicks));
            }
        }
        else {
            if (buttonMouseOverTimers[0] > 0F) {
                buttonMouseOverTimers[0] = Math.max(0F, buttonMouseOverTimers[0] - (buttonSelectionDecStep * partialTicks));
            }
        }
    }

    private void drawOptionsButton(final int mx, final int my, final float partialTicks) {
        if (buttonTimers[0] < buttonSpeed * 4F) {
            return;
        }

        prevButtonMouseOverDelta[1] = buttonMouseOverDeltas[1];
        buttonMouseOverDeltas[1] = Easings.easeInSine(buttonMouseOverTimers[1]);
        final float d = prevButtonMouseOverDelta[1] + (buttonMouseOverDeltas[1] - prevButtonMouseOverDelta[1]) * partialTicks;
        final float s = buttonSelectionScaleFactor * d;

        final float o = buttonTimers[1] < 1F ? prevButtonOffsets[1] + (buttonOffsets[1] - prevButtonOffsets[1]) * partialTicks : 1F;
        final float xo = (float) width - ((width >> 1) * o) + (buttonOffsetStep << 1);
        final float yo = (height >> 1) - (((currentButtonGap >> 1) + currentButtonGap) + discordButtonHeight);

        GlStateManager.pushMatrix();
        GlStateManager.translate(xo, yo, zLevel + 2F);
        GlStateManager.scale(currentButtonScale + s, currentButtonScale + s, 1F);

        final int color = ColorUtils.lerpRGBA(0xAAAAAAFF, 0xFFFFFFFF, d);
        final int[] channels = new int[4];
        ColorUtils.unpackRGBA(color, channels);
        final float c = (float) channels[0] / 255F;

        GlStateManager.color(c, c, c, 1F);

        GuiUtils.drawTexturedModalRect(0, 0, optionsButtonU, optionsButtonV, optionsButtonWidth, optionsButtonHeight, 0F);
        GlStateManager.popMatrix();

        if (buttonTimers[1] < 1F) {
            buttonTimers[1] += buttonSpeed * partialTicks;
            prevButtonOffsets[1] = buttonOffsets[1];
            buttonOffsets[1] = Easings.easeOutElastic(buttonTimers[1]);
        }
        else {
            final int actualWidth = (int) (optionsButtonWidth * currentButtonScale);
            final int actualHeight = (int) (optionsButtonHeight * currentButtonScale);

            final int minX = (int) xo;
            final int minY = (int) yo;
            final int maxX = minX + actualWidth;
            final int maxY = minY + actualHeight;
            isMouseOverButton[1] = (mx >= minX && mx <= maxX) && (my >= minY && my <= maxY);
        }

        if (isMouseOverButton[1]) {
            if (buttonMouseOverTimers[1] < 1F) {
                buttonMouseOverTimers[1] = Math.min(1F, buttonMouseOverTimers[1] + (buttonSelectionIncStep * partialTicks));
            }
        }
        else {
            if (buttonMouseOverTimers[1] > 0F) {
                buttonMouseOverTimers[1] = Math.max(0F, buttonMouseOverTimers[1] - (buttonSelectionDecStep * partialTicks));
            }
        }
    }

    private void drawDiscordButton(final int mx, final int my, final float partialTicks) {
        if (buttonTimers[1] < buttonSpeed * 4F) {
            return;
        }

        prevButtonMouseOverDelta[2] = buttonMouseOverDeltas[2];
        buttonMouseOverDeltas[2] = Easings.easeInSine(buttonMouseOverTimers[2]);
        final float d = prevButtonMouseOverDelta[2] + (buttonMouseOverDeltas[2] - prevButtonMouseOverDelta[2]) * partialTicks;
        final float s = buttonSelectionScaleFactor * d;

        final float o = buttonTimers[2] < 1F ? prevButtonOffsets[2] + (buttonOffsets[2] - prevButtonOffsets[2]) * partialTicks : 1F;
        final float xo = (float) width - ((width >> 1) * o) + (buttonOffsetStep * 3);
        final float yo = (height >> 1) - (currentButtonGap >> 1);

        GlStateManager.pushMatrix();
        GlStateManager.translate(xo, yo, zLevel + 2F);
        GlStateManager.scale(currentButtonScale + s, currentButtonScale + s, 1F);

        final int color = ColorUtils.lerpRGBA(0xAAAAAAFF, 0xFFFFFFFF, d);
        final int[] channels = new int[4];
        ColorUtils.unpackRGBA(color, channels);
        final float c = (float) channels[0] / 255F;

        GlStateManager.color(c, c, c, 1F);

        GuiUtils.drawTexturedModalRect(0, 0, discordButtonU, discordButtonV, discordButtonWidth, discordButtonHeight, 0F);
        GlStateManager.popMatrix();

        if (buttonTimers[2] < 1F) {
            buttonTimers[2] += buttonSpeed * partialTicks;
            prevButtonOffsets[2] = buttonOffsets[2];
            buttonOffsets[2] = Easings.easeOutElastic(buttonTimers[2]);
        }
        else {
            final int actualWidth = (int) (discordButtonWidth * currentButtonScale);
            final int actualHeight = (int) (discordButtonHeight * currentButtonScale);

            final int minX = (int) xo;
            final int minY = (int) yo;
            final int maxX = minX + actualWidth;
            final int maxY = minY + actualHeight;
            isMouseOverButton[2] = (mx >= minX && mx <= maxX) && (my >= minY && my <= maxY);
        }

        if (isMouseOverButton[2]) {
            if (buttonMouseOverTimers[2] < 1F) {
                buttonMouseOverTimers[2] = Math.min(1F, buttonMouseOverTimers[2] + (buttonSelectionIncStep * partialTicks));
            }
        }
        else {
            if (buttonMouseOverTimers[2] > 0F) {
                buttonMouseOverTimers[2] = Math.max(0F, buttonMouseOverTimers[2] - (buttonSelectionDecStep * partialTicks));
            }
        }
    }

    private void drawWebsiteButton(final int mx, final int my, final float partialTicks) {
        if (buttonTimers[2] < buttonSpeed * 4F) {
            return;
        }

        prevButtonMouseOverDelta[3] = buttonMouseOverDeltas[3];
        buttonMouseOverDeltas[3] = Easings.easeInSine(buttonMouseOverTimers[3]);
        final float d = prevButtonMouseOverDelta[3] + (buttonMouseOverDeltas[3] - prevButtonMouseOverDelta[3]) * partialTicks;
        final float s = buttonSelectionScaleFactor * d;

        final float o = buttonTimers[3] < 1F ? prevButtonOffsets[3] + (buttonOffsets[3] - prevButtonOffsets[3]) * partialTicks : 1F;
        final float xo = (float) width - ((width >> 1) * o) + (buttonOffsetStep << 2);
        final float yo = (height >> 1) + ((currentButtonGap >> 1) + discordButtonHeight);

        GlStateManager.pushMatrix();
        GlStateManager.translate(xo, yo, zLevel + 2F);
        GlStateManager.scale(currentButtonScale + s, currentButtonScale + s, 1F);

        final int color = ColorUtils.lerpRGBA(0xAAAAAAFF, 0xFFFFFFFF, d);
        final int[] channels = new int[4];
        ColorUtils.unpackRGBA(color, channels);
        final float c = (float) channels[0] / 255F;

        GlStateManager.color(c, c, c, 1F);

        GuiUtils.drawTexturedModalRect(0, 0, websiteButtonU, websiteButtonV, websiteButtonWidth, websiteButtonHeight, 0F);
        GlStateManager.popMatrix();

        if (buttonTimers[3] < 1F) {
            buttonTimers[3] += buttonSpeed * partialTicks;
            prevButtonOffsets[3] = buttonOffsets[3];
            buttonOffsets[3] = Easings.easeOutElastic(buttonTimers[3]);
        }
        else {
            final int actualWidth = (int) (websiteButtonWidth * currentButtonScale);
            final int actualHeight = (int) (websiteButtonHeight * currentButtonScale);

            final int minX = (int) xo;
            final int minY = (int) yo;
            final int maxX = minX + actualWidth;
            final int maxY = minY + actualHeight;
            isMouseOverButton[3] = (mx >= minX && mx <= maxX) && (my >= minY && my <= maxY);
        }

        if (isMouseOverButton[3]) {
            if (buttonMouseOverTimers[3] < 1F) {
                buttonMouseOverTimers[3] = Math.min(1F, buttonMouseOverTimers[3] + (buttonSelectionIncStep * partialTicks));
            }
        }
        else {
            if (buttonMouseOverTimers[3] > 0F) {
                buttonMouseOverTimers[3] = Math.max(0F, buttonMouseOverTimers[3] - (buttonSelectionDecStep * partialTicks));
            }
        }
    }

    private void drawQuitButton(final int mx, final int my, final float partialTicks) {
        if (buttonTimers[3] < buttonSpeed * 4F) {
            return;
        }

        prevButtonMouseOverDelta[4] = buttonMouseOverDeltas[4];
        buttonMouseOverDeltas[4] = Easings.easeInSine(buttonMouseOverTimers[4]);
        final float d = prevButtonMouseOverDelta[4] + (buttonMouseOverDeltas[4] - prevButtonMouseOverDelta[4]) * partialTicks;
        final float s = buttonSelectionScaleFactor * d;

        final float o = buttonTimers[4] < 1F ? prevButtonOffsets[4] + (buttonOffsets[4] - prevButtonOffsets[4]) * partialTicks : 1F;
        final float xo = (float) width - ((width >> 1) * o) + (buttonOffsetStep * 5);
        final float yo = (height >> 1) + (((currentButtonGap >> 1) + currentButtonGap) + discordButtonHeight + websiteButtonHeight);

        GlStateManager.pushMatrix();
        GlStateManager.translate(xo, yo, zLevel + 2F);
        GlStateManager.scale(currentButtonScale + s, currentButtonScale + s, 1F);

        final int color = ColorUtils.lerpRGBA(0xAAAAAAFF, 0xFFFFFFFF, d);
        final int[] channels = new int[4];
        ColorUtils.unpackRGBA(color, channels);
        final float c = (float) channels[0] / 255F;

        GlStateManager.color(c, c, c, 1F);

        GuiUtils.drawTexturedModalRect(0, 0, quitButtonU, quitButtonV, quitButtonWidth, quitButtonHeight, 0F);
        GlStateManager.popMatrix();

        if (buttonTimers[4] < 1F) {
            buttonTimers[4] += buttonSpeed * partialTicks;
            prevButtonOffsets[4] = buttonOffsets[4];
            buttonOffsets[4] = Easings.easeOutElastic(buttonTimers[4]);
        }
        else {
            final int actualWidth = (int) (quitButtonWidth * currentButtonScale);
            final int actualHeight = (int) (quitButtonHeight * currentButtonScale);

            final int minX = (int) xo;
            final int minY = (int) yo;
            final int maxX = minX + actualWidth;
            final int maxY = minY + actualHeight;
            isMouseOverButton[4] = (mx >= minX && mx <= maxX) && (my >= minY && my <= maxY);
        }

        if (isMouseOverButton[4]) {
            if (buttonMouseOverTimers[4] < 1F) {
                buttonMouseOverTimers[4] = Math.min(1F, buttonMouseOverTimers[4] + (buttonSelectionIncStep * partialTicks));
            }
        }
        else {
            if (buttonMouseOverTimers[4] > 0F) {
                buttonMouseOverTimers[4] = Math.max(0F, buttonMouseOverTimers[4] - (buttonSelectionDecStep * partialTicks));
            }
        }
    }

    private void drawDecoration(final float partialTicks) {
        final TextureManager textureManager = mc.getTextureManager();
        final float xs = (float) ((1D / (double) decorationWidth) * (double) width);
        final float ys = (float) ((1D / (double) decorationHeight) * (double) height);
        final float s = Math.min(xs, ys);
        final float a = prevDecorationAlpha + (decorationAlpha - prevDecorationAlpha) * partialTicks;

        GlStateManager.color(1F, 1F, 1F, a);
        textureManager.bindTexture(texture);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0F, 0F);
        GlStateManager.scale(s, s, 1F);
        GuiUtils.drawTexturedModalRect(0, 0, decorationU, decorationV, decorationWidth, decorationHeight, zLevel + 1F);
        GlStateManager.popMatrix();

        if (decorationTimer < 1F) {
            decorationTimer += decorationSpeed;
            prevDecorationAlpha = decorationAlpha;
            decorationAlpha = Easings.easeOutCirc(decorationTimer);
        }
    }

    private void drawBranding(final float partialTicks) {
        final float a = prevDecorationAlpha + (decorationAlpha - prevDecorationAlpha) * partialTicks;
        GlStateManager.color(1F, 1F, 1F, a);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) width - ((float) brandingWidth * 1.5F) - 6F, 6F, zLevel + 1F);
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
        GuiUtils.drawTexturedModalRect(0, 0, brandingU, brandingV, brandingWidth, brandingHeight, 0F);
        GlStateManager.popMatrix();
    }
}