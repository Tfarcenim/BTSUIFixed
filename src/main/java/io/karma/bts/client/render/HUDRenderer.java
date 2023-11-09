package io.karma.bts.client.render;

import io.karma.bts.client.ClientConfig;
import io.karma.bts.client.ClientEventHandler;
import io.karma.bts.client.screen.HUDConfigScreen;
import io.karma.bts.client.shader.*;
import io.karma.bts.common.BTSConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL13;

import java.util.function.Consumer;

public final class HUDRenderer {
    public static final int HUD_BASE_WIDTH = 250;
    public static final int HUD_BASE_HEIGHT = 84;
    public static final int HUD_FALL_SPEED = 2;
    public static final int HUD_RISE_SPEED = 1;

    public static final int HEALTH_BAR_BASE_WIDTH = 134;
    public static final int HEALTH_BAR_BASE_HEIGHT = 16;
    public static final int HEALTH_BAR_U = 0;
    public static final int HEALTH_BAR_V = 83;

    public static final int STAMINA_BAR_BASE_WIDTH = 120;
    public static final int STAMINA_BAR_BASE_HEIGHT = 15;
    public static final int STAMINA_BAR_U = 0;
    public static final int STAMINA_BAR_V = 99;

    public static final int XP_BAR_BASE_WIDTH = 204;
    public static final int XP_BAR_BASE_HEIGHT = 7;
    public static final int XP_BAR_U = 0;
    public static final int XP_BAR_V = 114;

    public static final int MONEY_BOX_BASE_WIDTH = 209;
    public static final int MONEY_BOX_BASE_HEIGHT = 56;
    public static final int MONEY_BOX_U = 0;
    public static final int MONEY_BOX_V = 122;
    public static final float MONEY_BOX_FONT_SCALE = 4F;
    public static final int MONEY_BOX_TEXT_OFFSET = 8;

    public static final int MONEY_SYMBOL_BASE_WIDTH = 28;
    public static final int MONEY_SYMBOL_BASE_HEIGHT = 43;
    public static final int MONEY_SYMBOL_U = 210;
    public static final int MONEY_SYMBOL_V = 122;
    public static final int MONEY_SYMBOL_BASE_X = (MONEY_BOX_BASE_WIDTH >> 1) - (MONEY_SYMBOL_BASE_WIDTH >> 1);
    public static final int MONEY_SYMBOL_BASE_Y = (MONEY_BOX_BASE_HEIGHT >> 1) - (MONEY_SYMBOL_BASE_HEIGHT >> 1);

    private static final ResourceLocation TEXTURE = new ResourceLocation(BTSConstants.MODID, "textures/gui/hud_elements.png");
    private static final ResourceLocation XP_BAR_SHADER = new ResourceLocation(BTSConstants.MODID, "shaders/gui/xp_bar.frag.glsl");
    private static final ResourceLocation HP_BAR_SHADER = new ResourceLocation(BTSConstants.MODID, "shaders/gui/hp_bar.frag.glsl");
    private static final ResourceLocation SP_BAR_SHADER = new ResourceLocation(BTSConstants.MODID, "shaders/gui/sp_bar.frag.glsl");

    private static final String U_TIME = "u_time";
    private static final String U_TEXTURE = "u_texture";
    private static final String U_HURT_FACTOR = "u_hurt_factor";
    private static final String U_SCALE = "u_scale";

    // @formatter:off
    private final ShaderProgram xpBarProgram = ShaderProgramBuilder.getInstance()
        .withObject(ShaderType.FRAGMENT, b -> b.fromResource(XP_BAR_SHADER))
        .withUniform(U_TIME, UniformType.FLOAT)
        .withUniform(U_TEXTURE, UniformType.INT)
        .withUniform(U_SCALE, UniformType.FLOAT)
        .withStaticUniforms(buffer -> buffer.set1i(U_TEXTURE, 0))
        .build();
    private final ShaderProgram hpBarProgram = ShaderProgramBuilder.getInstance()
        .withObject(ShaderType.FRAGMENT, b -> b.fromResource(HP_BAR_SHADER))
        .withUniform(U_TIME, UniformType.FLOAT)
        .withUniform(U_TEXTURE, UniformType.INT)
        .withUniform(U_SCALE, UniformType.FLOAT)
        .withUniform(U_HURT_FACTOR, UniformType.FLOAT)
        .withStaticUniforms(b -> b.set1i(U_TEXTURE, 0))
        .build();
    private final ShaderProgram spBarProgram = ShaderProgramBuilder.getInstance()
        .withObject(ShaderType.FRAGMENT, b -> b.fromResource(SP_BAR_SHADER))
        .withUniform(U_TIME, UniformType.FLOAT)
        .withUniform(U_TEXTURE, UniformType.INT)
        .withUniform(U_SCALE, UniformType.FLOAT)
        .withStaticUniforms(b -> b.set1i(U_TEXTURE, 0))
        .build();
    // @formatter:on

    public int xOffset;
    public int yOffset;
    public float scale;

    private final Consumer<UniformBuffer> SHARED_UNIFORMS = buffer -> {
        buffer.set1f(U_TIME, ClientEventHandler.INSTANCE.getRenderTime());
        buffer.set1f(U_SCALE, scale);
    };

    private int healthBarWidth;
    private int prevHealthBarWidth;
    private int staminaBarWidth;
    private int prevStaminaBarWidth;
    private int xpBarWidth;
    private int prevXpBarWidth;

    public HUDRenderer() {
        readFromConfig();
    }

    @SubscribeEvent
    public void onRenderGameOverlayPre(final @NotNull RenderGameOverlayEvent.Pre event) {
        final Minecraft mc = FMLClientHandler.instance().getClient();

        final EntityPlayer player = mc.player;

        if (player == null) {
            return;
        }

        switch (event.getType()) {
            case ALL:
                if (!(mc.currentScreen instanceof HUDConfigScreen) && !player.isCreative()) {
                    final ScaledResolution size = event.getResolution();
                    final int screenW = size.getScaledWidth();
                    final int screenH = size.getScaledHeight();
                    renderHUD(mc, player, 1F, event.getPartialTicks(), screenW, screenH);
                }
                break;
            case HEALTH:
            case EXPERIENCE:
            case FOOD:
                event.setCanceled(true);
                break;
        }
    }

    public void renderHUD(final @NotNull Minecraft mc, final @NotNull EntityPlayer player, final float zLevel, final float partialTicks, final int screenW, final int screenH) {
        final FontRenderer fontRenderer = mc.fontRenderer;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        // Fix bleeding color states -.-
        GlStateManager.color(1F, 1F, 1F, 1F);

        renderHPBar(player, fontRenderer, partialTicks, zLevel);
        renderSPBar(player, fontRenderer, partialTicks, zLevel);
        renderXPBar(player, fontRenderer, partialTicks, zLevel, screenW, screenH);

        // Fix bleeding color states -.-
        GlStateManager.color(1F, 1F, 1F, 1F);

        // Draw rest of HUD on top, so we hide shader artifacts.
        bindHUDTexture();
        GuiUtils.drawTexturedModalRect(xOffset, yOffset, 0, 0, HUD_BASE_WIDTH, HUD_BASE_HEIGHT, zLevel);

        // Money box
        GlStateManager.pushMatrix();
        GlStateManager.translate(xOffset, yOffset + 96, zLevel);
        GlStateManager.scale(0.75F, 0.75F, 0.75F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        bindHUDTexture();
        GuiUtils.drawTexturedModalRect(0, 0, MONEY_BOX_U, MONEY_BOX_V, MONEY_BOX_BASE_WIDTH, MONEY_BOX_BASE_HEIGHT, 0F);
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderHPBar(final @NotNull EntityPlayer player, final @NotNull FontRenderer fontRenderer, final float partialTicks, final float zLevel) {
        // Health bar
        final int newHealthBarWidth = (int) ((HEALTH_BAR_BASE_WIDTH / player.getMaxHealth()) * player.getHealth());

        // Guard against bars flickering when player is dead
        if (!player.isDead) {
            if (healthBarWidth < newHealthBarWidth) {
                healthBarWidth = Math.min(HEALTH_BAR_BASE_WIDTH, healthBarWidth + HUD_RISE_SPEED);
            }

            if (healthBarWidth > newHealthBarWidth) {
                healthBarWidth = Math.max(0, healthBarWidth - HUD_FALL_SPEED);
            }
        }

        float w = healthBarWidth + partialTicks * (prevHealthBarWidth - healthBarWidth);
        prevHealthBarWidth = healthBarWidth;
        int x = xOffset + 74;
        int y = yOffset + 36;

        bindHUDTexture();

        if (ClientConfig.enableHPShader) {
            final float hurtFactor = (player.getMaxHealth() - player.getHealth()) / player.getMaxHealth();

            hpBarProgram.use(SHARED_UNIFORMS.andThen(buffer -> {
                buffer.set1f(U_HURT_FACTOR, hurtFactor);
            }));

            GuiUtils.drawTexturedModalRect(x, y, HEALTH_BAR_U, HEALTH_BAR_V, (int) w, HEALTH_BAR_BASE_HEIGHT, zLevel);
            hpBarProgram.release();
        }
        else {
            GuiUtils.drawTexturedModalRect(x, y, HEALTH_BAR_U, HEALTH_BAR_V, (int) w, HEALTH_BAR_BASE_HEIGHT, zLevel);
        }

        String str = String.format("%.1f/%.1f HP", player.getHealth(), player.getMaxHealth());
        GlStateManager.translate(0F, 0F, zLevel + 1F);
        fontRenderer.drawStringWithShadow(str, x + 12, y + (HEALTH_BAR_BASE_HEIGHT >> 1) - (fontRenderer.FONT_HEIGHT >> 1) + 1, 0xFFFFFFFF);
        fontRenderer.drawStringWithShadow(TextFormatting.BOLD + player.getDisplayNameString(), x + 4, y - fontRenderer.FONT_HEIGHT - 3, 0xFFFFFFFF);
    }

    private void renderSPBar(final @NotNull EntityPlayer player, final @NotNull FontRenderer fontRenderer, final float partialTicks, final float zLevel) {
        // Stamina bar
        final int newStaminaBarWidth = (int) ((STAMINA_BAR_BASE_WIDTH / 20F) * 20); // TODO: implement this

        // Guard against bars flickering when player is dead
        if (!player.isDead) {
            if (staminaBarWidth < newStaminaBarWidth) {
                staminaBarWidth = Math.min(STAMINA_BAR_BASE_WIDTH, staminaBarWidth + HUD_RISE_SPEED);
            }

            if (staminaBarWidth > newStaminaBarWidth) {
                staminaBarWidth = Math.max(0, staminaBarWidth - HUD_FALL_SPEED);
            }
        }

        final float w = staminaBarWidth + partialTicks * (prevStaminaBarWidth - staminaBarWidth);
        prevStaminaBarWidth = staminaBarWidth;
        final int x = xOffset + 63;
        final int y = yOffset + 54;

        bindHUDTexture();

        if (ClientConfig.enableSPShader) {
            spBarProgram.use(SHARED_UNIFORMS);
            GuiUtils.drawTexturedModalRect(x, y, STAMINA_BAR_U, STAMINA_BAR_V, (int) w, STAMINA_BAR_BASE_HEIGHT, zLevel);
            spBarProgram.release();
        }
        else {
            GuiUtils.drawTexturedModalRect(x, y, STAMINA_BAR_U, STAMINA_BAR_V, (int) w, STAMINA_BAR_BASE_HEIGHT, zLevel);
        }

        final String str = String.format("%d/20 SP", player.getFoodStats().getFoodLevel());
        GlStateManager.translate(0F, 0F, zLevel + 1F);
        fontRenderer.drawStringWithShadow(str, x + 12, y + (STAMINA_BAR_BASE_HEIGHT >> 1) - (fontRenderer.FONT_HEIGHT >> 1) + 1, 0xFFFFFFFF);
    }

    private void renderXPBar(final @NotNull EntityPlayer player, final @NotNull FontRenderer fontRenderer, final float partialTicks, final float zLevel, final int screenW, final int screenH) {
        // Experience bar
        final int newXpBarWidth = (int) (XP_BAR_BASE_WIDTH * player.experience);

        // Guard against bars flickering when player is dead
        if (!player.isDead) {
            if (xpBarWidth < newXpBarWidth) {
                xpBarWidth = Math.min(XP_BAR_BASE_WIDTH, xpBarWidth + HUD_RISE_SPEED);
            }

            if (xpBarWidth > newXpBarWidth) {
                xpBarWidth = Math.max(0, xpBarWidth - HUD_FALL_SPEED);
            }
        }

        final float w = xpBarWidth + partialTicks * (prevXpBarWidth - xpBarWidth);
        prevXpBarWidth = xpBarWidth;
        final int x = xOffset + 44;
        final int y = yOffset + 73;

        bindHUDTexture();

        if (ClientConfig.enableXPShader) {
            xpBarProgram.use(SHARED_UNIFORMS);
            GuiUtils.drawTexturedModalRect(x, y, XP_BAR_U, XP_BAR_V, (int) w, XP_BAR_BASE_HEIGHT, zLevel);
            xpBarProgram.release();
        }
        else {
            GuiUtils.drawTexturedModalRect(x, y, XP_BAR_U, XP_BAR_V, (int) w, XP_BAR_BASE_HEIGHT, zLevel);
        }

        final String str = String.format("LvL. %d (%.1f%%)", player.experienceLevel, player.experience * 100F);
        fontRenderer.drawStringWithShadow(str, x, y + XP_BAR_BASE_HEIGHT + (fontRenderer.FONT_HEIGHT >> 1) + 1, 0xFF66FF22);
    }

    private void bindHUDTexture() {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
    }

    public void readFromConfig() {
        xOffset = ClientConfig.hudX;
        yOffset = ClientConfig.hudY;
        scale = (float) ClientConfig.hudScale;
    }

    public void writeToConfig() {
        ClientConfig.hudX= xOffset;
        ClientConfig.hudY= yOffset;
        ClientConfig.hudScale= scale;
    }

    public void reset() {
        staminaBarWidth = 0;
        prevStaminaBarWidth = 0;

        healthBarWidth = 0;
        prevHealthBarWidth = 0;

        xpBarWidth = 0;
        prevXpBarWidth = 0;
    }

    public int getScaledWidth() {
        return (int) (HUD_BASE_WIDTH * scale);
    }

    public int getScaledHeight() {
        return (int) (HUD_BASE_HEIGHT * scale);
    }

    public int getScaledXOffset() {
        return (int) (xOffset * scale);
    }

    public int getScaledYOffset() {
        return (int) (yOffset * scale);
    }
}
