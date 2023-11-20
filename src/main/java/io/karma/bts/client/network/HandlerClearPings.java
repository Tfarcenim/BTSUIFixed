package io.karma.bts.client.network;

import io.karma.bts.client.ClientProxy;
import io.karma.bts.common.BTSMod;
import io.karma.bts.server.network.PacketClearPings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.NotNull;

/**
 * Handles incoming {@link PacketClearPings} packets on the client.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
public final class HandlerClearPings implements IMessageHandler<PacketClearPings, IMessage> {
    @Override
    public IMessage onMessage(final @NotNull PacketClearPings message, final @NotNull MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            BTSMod.LOGGER.debug("Received PacketClearPings");
            ClientProxy.clearPingRenderer();
        });

        return null;
    }
}
