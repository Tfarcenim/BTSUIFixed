package io.karma.bts.client.network;

import io.karma.bts.client.ClientProxy;
import io.karma.bts.common.BTSMod;
import io.karma.bts.common.util.PingColor;
import io.karma.bts.server.network.PacketAddPing;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * Handles incoming {@link PacketAddPing} packets on the client.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
public final class HandlerAddPing implements IMessageHandler<PacketAddPing, IMessage> {
    @Override
    public @Nullable IMessage onMessage(final @NotNull PacketAddPing message, final @NotNull MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            final BlockPos pos = message.pos;
            final EnumSet<PingColor> colors = message.colors;

            BTSMod.LOGGER.debug("Received PacketAddPing for ping at [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());
            ClientProxy.addPingToRenderer(pos, colors);
        });

        return null;
    }
}
