package io.karma.bts.server.network;

import io.karma.bts.common.util.PingColor;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public final class PacketAddPing implements IMessage {
    public BlockPos pos = BlockPos.ORIGIN;
    public EnumSet<PingColor> colors = EnumSet.noneOf(PingColor.class);

    @Override
    public void fromBytes(final @NotNull ByteBuf buffer) {
        pos = BlockPos.fromLong(buffer.readLong());
        final int numColors = buffer.readInt();

        for (int i = 0; i < numColors; i++) {
            colors.add(PingColor.values()[buffer.readInt()]);
        }
    }

    @Override
    public void toBytes(final @NotNull ByteBuf buffer) {
        buffer.writeLong(pos.toLong());
        buffer.writeInt(colors.size());

        for (final PingColor color : colors) {
            buffer.writeInt(color.ordinal());
        }
    }
}
