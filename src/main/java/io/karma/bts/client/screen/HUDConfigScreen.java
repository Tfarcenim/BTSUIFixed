package io.karma.bts.client.screen;

import io.karma.bts.client.render.HUDRenderer;
import io.karma.bts.common.BTSConstants;
import io.karma.bts.common.BTSMod;
import io.karma.bts.common.util.InputUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class HUDConfigScreen extends BTSScreen {
    private static final int MOUSE_BUTTON_LEFT = 0;
    private static final int MOUSE_BUTTON_MIDDLE = 1;
    private static final int MOUSE_BUTTON_RIGHT = 2;

    private static final int posLeftButtonId = 0;
    private static final int posRightButtonId = 1;
    private static final int posDownButtonId = 2;
    private static final int posUpButtonId = 3;
    private static final int scaleUpButtonId = 4;
    private static final int scaleDownButtonId = 5;
    private static final int resetButtonId = 6;

    private final HUDRenderer renderer = new HUDRenderer();

    private boolean isMouseOverResizeCorner = false;
    private boolean isResizing = false;
    private boolean isMouseOverHud = false;
    private boolean isDragging = false;
    private int lastDragX;
    private int lastDragY;

    @Override
    public void drawScreen(final int mx, final int my, final float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mx, my, partialTicks);

        final int x = width - 78;
        final int y = height - 100;

        final FontRenderer fontRenderer = mc.fontRenderer;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, zLevel + 1F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        fontRenderer.drawStringWithShadow(I18n.format(String.format("label.%s.hud_config.ctrl_info_1", BTSConstants.MODID)), 0, 0, 0xFFFFFFFF);
        fontRenderer.drawStringWithShadow(I18n.format(String.format("label.%s.hud_config.ctrl_info_2", BTSConstants.MODID)), 0, fontRenderer.FONT_HEIGHT + 2, 0xFFFFFFFF);
        GlStateManager.popMatrix();

        final EntityPlayer player = mc.player;

        if (player == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.glLineWidth(1F);

        final int hudX = renderer.getScaledXOffset();
        final int hudY = renderer.getScaledYOffset();
        final int hudW = renderer.getScaledWidth();
        final int hudH = renderer.getScaledHeight();

        renderResizeBox(hudX, hudY, hudW, hudH, mx, my);
        renderResizeCorner(hudX + hudW, hudY + hudH, 3, 3, mx, my, 0xFFFFFFFF, 0xFFFF2222);

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();

        renderer.renderHUD(mc, player, 1F, partialTicks, width, height);
    }

    private void renderResizeCorner(final int cx, final int cy, final int w, final int h, final int mx, final int my, final int color, final int selectedColor) {
        final int hw = w >> 1;
        final int hh = h >> 1;
        final int x = cx - hw;
        final int y = cy - hh;
        int actualColor = color;

        if (((mx >= x && mx <= x + w) && (my >= y && my <= y + h)) || isResizing) {
            actualColor = selectedColor;
            isMouseOverResizeCorner = true;
        }
        else {
            isMouseOverResizeCorner = false;
        }

        GuiUtils.drawGradientRect((int) (zLevel + 3F), x, y, x + w, y + h, actualColor, actualColor);
    }

    private void renderResizeBox(final int x, final int y, final int w, final int h, final int mx, final int my) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(x, y, zLevel + 2F).color(1F, 1F, 1F, 1F).endVertex();
        buffer.pos(x + w, y, zLevel + 2F).color(1F, 1F, 1F, 1F).endVertex();

        buffer.pos(x + w, y, zLevel + 2F).color(1F, 1F, 1F, 1F).endVertex();
        buffer.pos(x + w, y + h, zLevel + 2F).color(1F, 1F, 1F, 1F).endVertex();

        buffer.pos(x + w, y + h, zLevel + 2F).color(1F, 1F, 1F, 1F).endVertex();
        buffer.pos(x, y + h, zLevel + 2F).color(1F, 1F, 1F, 1F).endVertex();

        buffer.pos(x, y + h, zLevel + 2F).color(1F, 1F, 1F, 1F).endVertex();
        buffer.pos(x, y, zLevel + 2F).color(1F, 1F, 1F, 1F).endVertex();

        tessellator.draw();

        if (((mx >= x && mx <= x + w) && (my >= y && my <= y + h) && !isMouseOverResizeCorner) || isDragging) {
            GuiUtils.drawGradientRect((int) zLevel, x, y, x + w, y + h, 0x66FFFFFF, 0x10FFFFFF);
            isMouseOverHud = true;
        }
        else {
            isMouseOverHud = false;
        }
    }

    @Override
    protected void mouseClicked(final int mx, final int my, final int button) throws IOException {
        super.mouseClicked(mx, my, button);

        if (button == MOUSE_BUTTON_LEFT) {
            if (isMouseOverResizeCorner) {
                isResizing = true;
            }

            if (isMouseOverHud) {
                if (!isDragging) {
                    lastDragX = mx;
                    lastDragY = my;
                    isDragging = true;
                }
            }
        }
    }

    @Override
    protected void mouseReleased(final int mx, final int my, final int state) {
        super.mouseReleased(mx, my, state);
        isResizing = false;
        isDragging = false;
        lastDragX = 0;
        lastDragY = 0;
    }

    @Override
    protected void mouseClickMove(final int mx, final int my, final int button, final long timeSinceLastClick) {
        super.mouseClickMove(mx, my, button, timeSinceLastClick);

        if (isDragging) {
            renderer.xOffset = (int) ((float) mx / renderer.scale);
            renderer.yOffset = (int) ((float) my / renderer.scale);
            return;
        }

        if (isResizing) {
            float sx = (float) (mx - renderer.getScaledXOffset()) / (float) HUDRenderer.HUD_BASE_WIDTH;
            float sy = (float) (my - renderer.getScaledYOffset()) / (float) HUDRenderer.HUD_BASE_HEIGHT;
            renderer.scale = Math.max(sx, sy); // TODO: definetly clip this..
        }
    }

    @Override
    public void initGui() {
        renderer.readFromConfig();

        final int x = width - 90;
        final int y = height - 90;

        addButton(new HUDConfigButton(this, posUpButtonId, x + 35, y + 5, 20, 20, "↑", String.format("tooltip.%s.hud_config.move_up", BTSConstants.MODID)));
        addButton(new HUDConfigButton(this, posLeftButtonId, x + 10, y + 30, 20, 20, "\u2190", String.format("tooltip.%s.hud_config.move_left", BTSConstants.MODID)));
        addButton(new HUDConfigButton(this, posRightButtonId, x + 60, y + 30, 20, 20, "→", String.format("tooltip.%s.hud_config.move_right", BTSConstants.MODID)));
        addButton(new HUDConfigButton(this, posDownButtonId, x + 35, y + 55, 20, 20, "↓", String.format("tooltip.%s.hud_config.move_down", BTSConstants.MODID)));

        addButton(new HUDConfigButton(this, scaleUpButtonId, x + 10, y + 5, 20, 20, "+", String.format("tooltip.%s.hud_config.scale_up", BTSConstants.MODID)));
        addButton(new HUDConfigButton(this, scaleDownButtonId, x + 60, y + 5, 20, 20, "-", String.format("tooltip.%s.hud_config.scale_down", BTSConstants.MODID)));

        addButton(new HUDConfigButton(this, resetButtonId, x + 35, y + 30, 20, 20, "R", String.format("tooltip.%s.hud_config.reset", BTSConstants.MODID)));
    }

    @Override
    public void onGuiClosed() {
        renderer.writeToConfig();
        BTSMod.PROXY.requestHUDUpdate();

        final EntityPlayer player = mc.player;

        if (player != null) {
            player.sendStatusMessage(new TextComponentTranslation(String.format("status.%s.hud_config_saved", BTSConstants.MODID)), true);
        }
    }

    @Override
    protected void actionPerformed(final @NotNull GuiButton button) {
        final boolean isShiftDown = InputUtils.isShiftDown();
        final int posStepSize = isShiftDown ? 10 : 1;
        final float scaleStepSize = isShiftDown ? 0.25F : 0.025F;

        switch (button.id) {
            case posUpButtonId:
                if (renderer.yOffset > 0) {
                    renderer.yOffset = Math.max(0, renderer.yOffset - posStepSize);
                }
                break;
            case posLeftButtonId:
                if (renderer.xOffset > 0) {
                    renderer.xOffset = Math.max(0, renderer.xOffset - posStepSize);
                }
                break;
            case posRightButtonId:
                final int maxX = width - renderer.getScaledWidth();

                if (renderer.xOffset < maxX) {
                    renderer.xOffset = Math.min(maxX, renderer.xOffset + posStepSize);
                }
                break;
            case posDownButtonId:
                final int maxY = height - renderer.getScaledHeight();

                if (renderer.yOffset < maxY) {
                    renderer.yOffset = Math.min(maxY, renderer.yOffset + posStepSize);
                }
                break;
            case scaleUpButtonId:
                if (renderer.scale < 2F) {
                    renderer.scale += scaleStepSize;
                }
                break;
            case scaleDownButtonId:
                if (renderer.scale > 0F) {
                    renderer.scale -= scaleStepSize;
                }
                break;
            case resetButtonId:
                renderer.xOffset = 10;
                renderer.yOffset = 10;
                renderer.scale = 0.75F;
                break;
        }
    }

    public static final class HUDConfigButton extends GuiButton {
        private final GuiScreen parent;
        private final List<String> tooltipLines;

        public HUDConfigButton(final @NotNull GuiScreen parent, final int buttonId, final int x, final int y, final @NotNull String buttonText, final @NotNull String tooltipKey) {
            this(parent, buttonId, x, y, 200, 20, buttonText, tooltipKey);
        }

        public HUDConfigButton(final @NotNull GuiScreen parent, final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final @NotNull String buttonText, final @NotNull String tooltipKey) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.parent = parent;
            tooltipLines = Arrays.asList(I18n.format(tooltipKey).split("\n"));
        }

        @Override
        public void drawButton(final @NotNull Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
            super.drawButton(mc, mouseX, mouseY, partialTicks);

            if (isMouseOver() && InputUtils.isCtrlDown()) {
                final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                final int w = parent.width;
                final int h = parent.height;

                GlStateManager.pushMatrix();
                GlStateManager.translate(0F, 0F, zLevel * 4F);

                GuiUtils.drawHoveringText(tooltipLines, w, 20, w, h, 200, fontRenderer);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();

                GlStateManager.popMatrix();
            }
        }
    }
}
