package io.karma.bts.client.screen;

import io.karma.bts.client.screen.shapes.Polygon;
import io.karma.bts.common.BTSConstants;
import io.karma.bts.common.BTSMod;
import io.karma.bts.server.network.RunCommandPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import io.karma.bts.joml.Vector2i;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 14/08/2022
 */
public class QuickMenuScreen extends BTSScreen {

    protected final static int EXIT = 999;
    protected double backGroundScale = .5;
    protected final ResourceLocation background = BUTTONS;//new ResourceLocation(BTSConstants.MODID,"textures/gui/wheel_gui.png");
    private static final ResourceLocation BUTTONS = new ResourceLocation(BTSConstants.MODID, "textures/gui/wheel_gui_buttons.png");
    protected final int backgroundTextureSizeX;
    protected final int backgroundTextureSizeY;


    protected int xSize = 176;
    protected int ySize = 166;


    protected int guiLeft;
    protected int guiTop;

    public QuickMenuScreen() {
        backgroundTextureSizeX = 512;
        backgroundTextureSizeY = 512;
        Minecraft.getMinecraft().world.playSound(Minecraft.getMinecraft().player.getPosition(),
                SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.5f, 0.9f,false);
    }


    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        drawDefaultBackground();
        drawBackgroundLayer(partialTicks, mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawForegroundLayer(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();


        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        btn0();
        btn1();
        btn2();
        btn3();
        btn4();
        btn5();
        btn6();
        btn7();
        btnSpawn();
    }

    protected void btn0() {
        Polygon polygon = new Polygon();

        polygon.addPoint(guiLeft + 97, guiTop + 12);
        polygon.addPoint(guiLeft + 115, guiTop + 16);
        polygon.addPoint(guiLeft + 131, guiTop + 25);
        polygon.addPoint(guiLeft + 129, guiTop + 28);
        polygon.addPoint(guiLeft + 129, guiTop + 35);
        polygon.addPoint(guiLeft + 104, guiTop + 62);
        polygon.addPoint(guiLeft + 92, guiTop + 57);
        polygon.addPoint(guiLeft + 92, guiTop + 23);
        polygon.addPoint(guiLeft + 97, guiTop + 17);

        addButton(new ShapedButtonDebug(0, 0, 0, 25, 25, "", polygon));
    }

    protected void btn1() {

        Polygon polygon = new Polygon();

        polygon.addPoint(guiLeft + 145, guiTop + 41);
        polygon.addPoint(guiLeft + 154, guiTop + 56);
        polygon.addPoint(guiLeft + 158, guiTop + 75);
        polygon.addPoint(guiLeft + 154, guiTop + 74);
        polygon.addPoint(guiLeft + 150, guiTop + 78);
        polygon.addPoint(guiLeft + 115, guiTop + 79);
        polygon.addPoint(guiLeft + 114, guiTop + 73);
        polygon.addPoint(guiLeft + 111, guiTop + 69);
        polygon.addPoint(guiLeft + 135, guiTop + 43);
        polygon.addPoint(guiLeft + 143, guiTop + 44);

        addButton(new ShapedButtonDebug(1, 0, 0, 25, 25, "", polygon));
    }

    protected void btn2() {
        Polygon polygon = new Polygon();
        polygon.addPoint(guiLeft + 115, guiTop + 89);
        polygon.addPoint(guiLeft + 151, guiTop + 90);
        polygon.addPoint(guiLeft + 154, guiTop + 93);
        polygon.addPoint(guiLeft + 157, guiTop + 93);
        polygon.addPoint(guiLeft + 156, guiTop + 105);
        polygon.addPoint(guiLeft + 151, guiTop + 119);
        polygon.addPoint(guiLeft + 145, guiTop + 126);
        polygon.addPoint(guiLeft + 142, guiTop + 124);
        polygon.addPoint(guiLeft + 136, guiTop + 124);
        polygon.addPoint(guiLeft + 110, guiTop + 100);
        polygon.addPoint(guiLeft + 114, guiTop + 95);

        addButton(new ShapedButtonDebug(2, 0, 0, 25, 25, "", polygon));
    }

    protected void btn3() {
        Polygon polygon = new Polygon();

        polygon.addPoint(guiLeft + 103, guiTop + 107);
        polygon.addPoint(guiLeft + 128, guiTop + 132);
        polygon.addPoint(guiLeft + 127, guiTop + 139);
        polygon.addPoint(guiLeft + 130, guiTop + 141);
        polygon.addPoint(guiLeft + 129, guiTop + 143);
        polygon.addPoint(guiLeft + 118, guiTop + 150);
        polygon.addPoint(guiLeft + 100, guiTop + 155);
        polygon.addPoint(guiLeft + 98, guiTop + 155);
        polygon.addPoint(guiLeft + 97, guiTop + 149);
        polygon.addPoint(guiLeft + 93, guiTop + 147);
        polygon.addPoint(guiLeft + 94, guiTop + 111);
        polygon.addPoint(guiLeft + 99, guiTop + 110);

        addButton(new ShapedButtonDebug(3, 0, 0, 25, 25, "", polygon));
    }

    protected void btn4() {
        Polygon polygon = new Polygon();
        polygon.addPoint(guiLeft + 73, guiTop + 108);
        polygon.addPoint(guiLeft + 76, guiTop + 111);
        polygon.addPoint(guiLeft + 81, guiTop + 112);
        polygon.addPoint(guiLeft + 82, guiTop + 147);
        polygon.addPoint(guiLeft + 79, guiTop + 150);
        polygon.addPoint(guiLeft + 79, guiTop + 154);
        polygon.addPoint(guiLeft + 65, guiTop + 155);
        polygon.addPoint(guiLeft + 45, guiTop + 143);
        polygon.addPoint(guiLeft + 47, guiTop + 139);
        polygon.addPoint(guiLeft + 47, guiTop + 132);
        addButton(new ShapedButtonDebug(4, 0, 0, 25, 25, "", polygon));
    }

    protected void btn5() {
        Polygon polygon = new Polygon();
        polygon.addPoint(guiLeft + 25, guiTop + 90);
        polygon.addPoint(guiLeft + 59, guiTop + 90);
        polygon.addPoint(guiLeft + 61, guiTop + 95);
        polygon.addPoint(guiLeft + 65, guiTop + 99);
        polygon.addPoint(guiLeft + 39, guiTop + 124);
        polygon.addPoint(guiLeft + 33, guiTop + 124);
        polygon.addPoint(guiLeft + 30, guiTop + 126);
        polygon.addPoint(guiLeft + 22, guiTop + 113);
        polygon.addPoint(guiLeft + 17, guiTop + 93);
        polygon.addPoint(guiLeft + 21, guiTop + 93);

        addButton(new ShapedButtonDebug(5, 0, 0, 25, 25, "", polygon));
    }

    protected void btn6() {
        Polygon polygon = new Polygon();

        polygon.addPoint(guiLeft + 31, guiTop + 40);
        polygon.addPoint(guiLeft + 32, guiTop + 43);
        polygon.addPoint(guiLeft + 39, guiTop + 43);
        polygon.addPoint(guiLeft + 65, guiTop + 69);
        polygon.addPoint(guiLeft + 60, guiTop + 73);
        polygon.addPoint(guiLeft + 60, guiTop + 78);
        polygon.addPoint(guiLeft + 25, guiTop + 78);
        polygon.addPoint(guiLeft + 22, guiTop + 75);
        polygon.addPoint(guiLeft + 17, guiTop + 74);
        polygon.addPoint(guiLeft + 18, guiTop + 62);
        addButton(new ShapedButtonDebug(6, 0, 0, 25, 25, "", polygon));
    }

    protected void btn7() {
        Polygon polygon = new Polygon();

        polygon.addPoint(guiLeft + 78, guiTop + 13);
        polygon.addPoint(guiLeft + 78, guiTop + 18);
        polygon.addPoint(guiLeft + 82, guiTop + 20);
        polygon.addPoint(guiLeft + 81, guiTop + 56);
        polygon.addPoint(guiLeft + 76, guiTop + 57);
        polygon.addPoint(guiLeft + 73, guiTop + 61);
        polygon.addPoint(guiLeft + 48, guiTop + 36);
        polygon.addPoint(guiLeft + 47, guiTop + 28);
        polygon.addPoint(guiLeft + 45, guiTop + 25);
        polygon.addPoint(guiLeft + 65, guiTop + 14);
        addButton(new ShapedButtonDebug(7, 0, 0, 25, 25, "", polygon));
    }

    protected void btnSpawn() {
        Polygon polygon = Polygon.makeSimpleDiamond(33, 33, new Vector2i(guiLeft + 88, guiTop + 84));
        addButton(new ShapedButtonDebug(8, 0, 0, 25, 25, "", polygon));
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        String s = "polygon.addPoint(guiLeft + " + (mouseX - guiLeft) + ",guiTop + " + (mouseY - guiTop) + ");";
        System.out.println(s);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id >= 0 && button.id < 9) {
            BTSMod.CHANNEL.sendToServer(new RunCommandPacket(button.id));
        }
    }

    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int w = 411;
        int h = 409;

        int backGroundSizeX = (int) (backgroundTextureSizeX * backGroundScale);
        int backGroundSizeY = (int) (backgroundTextureSizeY * backGroundScale);

        int i = (this.width - backGroundSizeX) / 2;
        int j = (this.height - backGroundSizeY) / 2;
        drawScaledCustomSizeModalRect(i, j, 0, 0, w, h, backGroundSizeX, backGroundSizeY, backgroundTextureSizeX, backgroundTextureSizeY);
    }

    protected void drawForegroundLayer(int mouseX, int mouseY, float partialTicks) {

    }

}
