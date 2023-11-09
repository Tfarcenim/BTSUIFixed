package io.karma.bts.common.util;

import io.karma.bts.common.BTSConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public enum PingColor {
    // @formatter:off
    BLUE    (new ResourceLocation(BTSConstants.MODID, "textures/ping/ping_blue.png")),
    GREEN   (new ResourceLocation(BTSConstants.MODID, "textures/ping/ping_green.png")),
    YELLOW  (new ResourceLocation(BTSConstants.MODID, "textures/ping/ping_yellow.png")),
    RED     (new ResourceLocation(BTSConstants.MODID, "textures/ping/ping_red.png"));
    // @formatter:on

    private final ResourceLocation texture;

    PingColor(final @NotNull ResourceLocation texture) {
        this.texture = texture;
    }

    @SideOnly(Side.CLIENT)
    public @NotNull ResourceLocation getTexture() {
        return texture;
    }
}
