package io.karma.bts.server.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.jetbrains.annotations.NotNull;

public final class PacketClearPings implements IMessage {
    @Override
    public void fromBytes(final @NotNull ByteBuf buffer) {
    }

    @Override
    public void toBytes(final @NotNull ByteBuf buffer) {
    }
}
