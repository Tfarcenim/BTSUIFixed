package io.karma.bts.common.util;

import io.karma.bts.common.registry.Register;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.EnumSet;

/**
 * Simple {@link EnumSet} serializer for attaching ping information
 * to the player data.
 *
 * @author KitsuneAlex
 * @since 23/06/2022
 */
public final class PingColorSerializer implements DataSerializer<EnumSet<PingColor>> {
    @Register("ping_color")
    public static final PingColorSerializer INSTANCE = new PingColorSerializer();

    // @formatter:off
    private PingColorSerializer() {}
    // @formatter:on

    @Override
    public void write(final @NotNull PacketBuffer buf, final @NotNull EnumSet<PingColor> value) {
        buf.writeInt(value.size());

        for (final PingColor color : value) {
            buf.writeInt(color.ordinal());
        }
    }

    @Override
    public @NotNull EnumSet<PingColor> read(final @NotNull PacketBuffer buf) throws IOException {
        final int count = buf.readInt();
        final EnumSet<PingColor> colors = EnumSet.noneOf(PingColor.class);

        for (int i = 0; i < count; i++) {
            colors.add(PingColor.values()[buf.readInt()]);
        }

        return colors;
    }

    @Override
    public @NotNull DataParameter<EnumSet<PingColor>> createKey(final int id) {
        return new DataParameter<>(id, this);
    }

    @Override
    public @NotNull EnumSet<PingColor> copyValue(final @NotNull EnumSet<PingColor> value) {
        return EnumSet.copyOf(value);
    }
}
