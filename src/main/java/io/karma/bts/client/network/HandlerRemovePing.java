package io.karma.bts.client.network;

import io.karma.bts.common.BTSMod;
import io.karma.bts.common.util.PingColor;
import io.karma.bts.server.network.PacketRemovePing;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Handles incoming {@link PacketRemovePing} packets on the client.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
public final class HandlerRemovePing implements IMessageHandler<PacketRemovePing, IMessage> {
    @Override
    public @Nullable IMessage onMessage(final @NotNull PacketRemovePing message, final @NotNull MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            final BlockPos pos = message.pos;
            final EnumSet<PingColor> colors = message.colors;

            BTSMod.LOGGER.debug("Received PacketRemovePing for ping at [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());
            BTSMod.PROXY.removePingFromRenderer(pos, colors);
        });

        return null;
    }
}
