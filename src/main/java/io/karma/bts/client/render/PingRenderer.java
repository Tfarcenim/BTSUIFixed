package io.karma.bts.client.render;

import io.karma.bts.client.ClientConfig;
import io.karma.bts.client.ClientEventHandler;
import io.karma.bts.client.shader.*;
import io.karma.bts.common.BTSConstants;
import io.karma.bts.common.util.BlockPosUtils;
import io.karma.bts.common.util.PingColor;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

public final class PingRenderer {
    public static final int PING_BASE_SIZE = 128;
    public static final int PING_HALF_SIZE = PING_BASE_SIZE >> 1;
    public static final int PING_THIRD_SIZE = PING_BASE_SIZE / 3;
    public static final int PING_Y_CENTER = 54;

    public static final float PING_UV_UNIT = 1F / (float) PING_BASE_SIZE;
    public static final float TRI_COLOR_UV_OFFSET = PING_UV_UNIT * 6F;
    public static final float SCALE_FACTOR = 0.2F;
    public static final float ONE_THIRD = 1F / 3F;
    public static final float TWO_THIRDS = ONE_THIRD * 2F;

    private static final String U_TIME = "u_time";
    private static final ResourceLocation SHADER = new ResourceLocation(BTSConstants.MODID, "shaders/ping.frag.glsl");

    private static final Consumer<UniformBuffer> SHARED_UNIFORMS = buffer -> {
        buffer.set1f(U_TIME, ClientEventHandler.INSTANCE.getRenderTime());
    };

    private final Long2ObjectOpenHashMap<EnumSet<PingColor>> pings = new Long2ObjectOpenHashMap<>();
    private final ArrayList<OrderedPing> sortedPings = new ArrayList<>();

    // @formatter:off
    private final ShaderProgram shader = ShaderProgramBuilder.getInstance()
        .withObject(ShaderType.FRAGMENT, b -> b.fromResource(SHADER))
        .withUniform("u_time", UniformType.FLOAT)
        .build();
    // @formatter:on

    public float scale;
    public float opacity;

    public PingRenderer() {
        readFromConfig();
    }

    public void clearPings() {
        pings.clear();
        sortedPings.clear();
    }

    public void addPing(final long pos, final @Nullable EnumSet<PingColor> colors) {
        if (colors == null) {
            return;
        }

        EnumSet<PingColor> colorSet = pings.get(pos);

        if (colorSet == null) {
            pings.put(pos, EnumSet.copyOf(colors));
            return;
        }

        colorSet.addAll(colors);
    }

    public void removePing(final long pos, final @Nullable EnumSet<PingColor> colors) {
        if (colors == null || !pings.containsKey(pos)) {
            return;
        }

        pings.get(pos).removeAll(colors);
    }

    private void sortPings(final @NotNull EntityPlayer player) {
        sortedPings.clear();

        float maxDistance = (float) ClientConfig.pingRenderDistance;
        maxDistance *= maxDistance;

        final float px = (float) player.posX + 0.5F;
        final float py = (float) player.posY + 0.5F;
        final float pz = (float) player.posZ + 0.5F;

        final Set<Long2ObjectOpenHashMap.Entry<EnumSet<PingColor>>> entries = pings.long2ObjectEntrySet();
        MutableBlockPos pos = new MutableBlockPos(0, 0, 0);

        for (final Long2ObjectOpenHashMap.Entry<EnumSet<PingColor>> ping : entries) {
            final long serializedPos = ping.getLongKey();
            BlockPosUtils.setFromLong(pos, serializedPos);

            final float bx = (float) pos.getX() + 0.5F;
            final float by = (float) pos.getY() + 0.5F;
            final float bz = (float) pos.getZ() + 0.5F;

            final float dx = bx - px;
            final float dy = by - py;
            final float dz = bz - pz;

            final float d = (dx * dx) + (dy * dy) + (dz * dz);

            if (d >= maxDistance) {
                continue;
            }

            sortedPings.add(new OrderedPing(serializedPos, ping.getValue().toArray(new PingColor[0]), d));
        }

        Collections.sort(sortedPings); // Since we can't use GL depth, sort pings by depth ourselves
    }

    @SubscribeEvent
    public void onRenderWorldLast(final @NotNull RenderWorldLastEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.player;

        if (player == null) {
            return;
        }

        final float partialTicks = event.getPartialTicks();
        sortPings(player);

        MutableBlockPos pos = new MutableBlockPos(0, 0, 0);

        for (final OrderedPing ping : sortedPings) {
            BlockPosUtils.setFromLong(pos, ping.pos);
            renderPing(player, pos, ping, partialTicks);
        }
    }

    private void renderPing(final @NotNull EntityPlayer player, final @NotNull BlockPos pos, final @NotNull OrderedPing ping, float partialTicks) {
        final PingColor[] colors = ping.colors;
        final int numColors = colors.length;

        if (numColors > 0) {
            final Minecraft mc = Minecraft.getMinecraft();
            final TextureManager textureManager = mc.getTextureManager();
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder buffer = tessellator.getBuffer();

            final float x = (pos.getX() + 0.5F) - (float) TileEntityRendererDispatcher.staticPlayerX;
            final float y = (pos.getY() + 0.5F) - (float) TileEntityRendererDispatcher.staticPlayerY;
            final float z = (pos.getZ() + 0.5F) - (float) TileEntityRendererDispatcher.staticPlayerZ;

            final TileEntityRendererDispatcher terd = TileEntityRendererDispatcher.instance;
            final float yaw = terd.entityYaw;
            final float pitch = terd.entityPitch;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            final float s = (scale * SCALE_FACTOR) * Math.max(1F, (float) Math.sqrt(ping.distance) * SCALE_FACTOR);
            GlStateManager.scale(s, s, s);

            GlStateManager.glNormal3f(0F, 1F, 0F);
            GlStateManager.rotate(-yaw, 0F, 1F, 0F);
            GlStateManager.rotate(pitch, 1F, 0F, 0F);
            GlStateManager.scale(-0.025F, -0.025F, 0.025F);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

            switch (numColors) {
                case 1:
                    textureManager.bindTexture(colors[0].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(-PING_HALF_SIZE, -PING_HALF_SIZE, 0D).tex(0D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(-PING_HALF_SIZE, PING_HALF_SIZE, 0D).tex(0D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, PING_HALF_SIZE, 0D).tex(1D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, -PING_HALF_SIZE, 0D).tex(1D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();
                    break;
                case 2:
                    textureManager.bindTexture(colors[0].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(-PING_HALF_SIZE, -PING_HALF_SIZE, 0D).tex(0D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(-PING_HALF_SIZE, PING_HALF_SIZE, 0D).tex(0D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, PING_HALF_SIZE, 0D).tex(0.5D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, -PING_HALF_SIZE, 0D).tex(0.5D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();

                    textureManager.bindTexture(colors[1].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(0D, -PING_HALF_SIZE, 0D).tex(0.5D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, PING_HALF_SIZE, 0D).tex(0.5D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, PING_HALF_SIZE, 0D).tex(1D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, -PING_HALF_SIZE, 0D).tex(1D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();
                    break;
                case 3:
                    textureManager.bindTexture(colors[0].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(-PING_HALF_SIZE, -PING_HALF_SIZE, 0D).tex(0D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(-PING_HALF_SIZE, PING_HALF_SIZE, 0D).tex(0D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(-PING_HALF_SIZE + PING_THIRD_SIZE + 6D, PING_HALF_SIZE, 0D).tex(ONE_THIRD + TRI_COLOR_UV_OFFSET, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(-PING_HALF_SIZE + PING_THIRD_SIZE + 6D, -PING_HALF_SIZE, 0D).tex(ONE_THIRD + TRI_COLOR_UV_OFFSET, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();

                    textureManager.bindTexture(colors[1].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(-PING_HALF_SIZE + PING_THIRD_SIZE + 6D, -PING_HALF_SIZE, 0D).tex(ONE_THIRD + TRI_COLOR_UV_OFFSET, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(-PING_HALF_SIZE + PING_THIRD_SIZE + 6D, PING_HALF_SIZE, 0D).tex(ONE_THIRD + TRI_COLOR_UV_OFFSET, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE - PING_THIRD_SIZE - 6D, PING_HALF_SIZE, 0D).tex(TWO_THIRDS - TRI_COLOR_UV_OFFSET, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE - PING_THIRD_SIZE - 6D, -PING_HALF_SIZE, 0D).tex(TWO_THIRDS - TRI_COLOR_UV_OFFSET, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();

                    textureManager.bindTexture(colors[2].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(PING_HALF_SIZE - PING_THIRD_SIZE - 6D, -PING_HALF_SIZE, 0D).tex(TWO_THIRDS - TRI_COLOR_UV_OFFSET, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE - PING_THIRD_SIZE - 6D, PING_HALF_SIZE, 0D).tex(TWO_THIRDS - TRI_COLOR_UV_OFFSET, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, PING_HALF_SIZE, 0D).tex(1D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, -PING_HALF_SIZE, 0D).tex(1D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();
                    break;
                case 4:
                    double m = -PING_HALF_SIZE + PING_Y_CENTER;
                    double u = PING_Y_CENTER * PING_UV_UNIT;

                    textureManager.bindTexture(colors[0].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(-PING_HALF_SIZE, -PING_HALF_SIZE, 0D).tex(0D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(-PING_HALF_SIZE, m, 0D).tex(0D, u).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, m, 0D).tex(0.5D, u).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, -PING_HALF_SIZE, 0D).tex(0.5D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();

                    textureManager.bindTexture(colors[1].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(0D, -PING_HALF_SIZE, 0D).tex(0.5D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, m, 0D).tex(0.5D, u).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, m, 0D).tex(1D, u).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, -PING_HALF_SIZE, 0D).tex(1D, 0D).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();

                    textureManager.bindTexture(colors[2].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(-PING_HALF_SIZE, m, 0D).tex(0D, u).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(-PING_HALF_SIZE, PING_HALF_SIZE, 0D).tex(0D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, PING_HALF_SIZE, 0D).tex(0.5D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, m, 0D).tex(0.5D, u).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();

                    textureManager.bindTexture(colors[3].getTexture());
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                    buffer.pos(0D, m, 0D).tex(0.5D, u).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(0D, PING_HALF_SIZE, 0D).tex(0.5D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, PING_HALF_SIZE, 0D).tex(1D, 1D).color(1F, 1F, 1F, opacity).endVertex();
                    buffer.pos(PING_HALF_SIZE, m, 0D).tex(1D, u).color(1F, 1F, 1F, opacity).endVertex();
                    tessellator.draw();
                    break;
            }

            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    public void readFromConfig() {
        scale = (float) ClientConfig.pingScale;
        opacity = (float) ClientConfig.pingOpacity;
    }

    public void writeToConfig() {
        ClientConfig.pingScale=(scale);
        ClientConfig.pingOpacity=(opacity);
    }

    public static class OrderedPing implements Comparable<OrderedPing> {
        public final long pos;
        public final PingColor[] colors;
        public final float distance;

        OrderedPing(final long pos, final @NotNull PingColor[] colors, final float distance) {
            this.pos = pos;
            this.colors = colors;
            this.distance = distance;
        }

        @Override
        public int compareTo(final @NotNull OrderedPing other) {
            return Float.compare(other.distance, distance);
        }
    }
}
