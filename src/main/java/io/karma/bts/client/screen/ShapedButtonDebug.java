package io.karma.bts.client.screen;

import io.karma.bts.client.screen.shapes.Polygon;
import io.karma.bts.client.screen.shapes.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.joml.Vector2i;

public class ShapedButtonDebug extends GuiButton {

    protected final Polygon polygon;

    public static final boolean DEBUG = true;

    public ShapedButtonDebug(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText,Polygon polygon) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.polygon = polygon;

    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            int j = 14737632;
            FontRenderer fontrenderer = mc.fontRenderer;
        /*    mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = isWithinShape(mouseX,mouseY);
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);

            if (packedFGColour != 0)
            {
                j = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }*/


            if (false) {
                drawDebugBox(mc);
            }
        }
    }

    public void drawDebugBox(Minecraft mc) {
        Rectangle rectangle = polygon.getEnclosing();
        if (rectangle == null) return;
        for (int i = 0; i < polygon.getPoints().size(); i++) {
            Vector2i point1 = polygon.getPoints().get(i);
            Vector2i point2;
            if (i == polygon.getPoints().size() - 1) {
                point2 = polygon.getPoints().get(0);
            } else {
                point2 = polygon.getPoints().get(i + 1);
            }
            drawLine(point1.x, point1.y, point2.x, point2.y, 0xff00ff00);
        }

        drawHorizontalLine(rectangle.point1.x, rectangle.point2.x, rectangle.point1.y, 0xffffffff);
        drawHorizontalLine(rectangle.point1.x, rectangle.point2.x, rectangle.point2.y, 0xffffffff);
        drawVerticalLine(rectangle.point1.x, rectangle.point1.y, rectangle.point2.y, 0xffffffff);
        drawVerticalLine(rectangle.point2.x, rectangle.point1.y, rectangle.point2.y, 0xffffffff);

        int j = 0xe0e0e0;
        FontRenderer fontrenderer = mc.fontRenderer;

        int x1 = (rectangle.point1.x + rectangle.point2.x) / 2;
        int y1 = (rectangle.point1.y + rectangle.point2.y) / 2;
        this.drawCenteredString(fontrenderer, this.displayString, x1, y1 - 4, j);
    }

    public static void drawLine(int x1, int y1, int x2, int y2, int color) {

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(f, f1, f2, f3);
        GlStateManager.glLineWidth(2.0F);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x1, y1, 0.0D).endVertex();
        bufferbuilder.pos(x2, y2, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && isWithinShape(mouseX,mouseY);
    }

    public boolean isWithinShape(int mouseX, int mouseY) {
        return polygon.contains(mouseX,mouseY);//mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }
}
