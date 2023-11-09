package io.karma.bts.common.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import org.jetbrains.annotations.Nullable;

public final class BlockPosUtils {
    // @formatter:off
    private BlockPosUtils() {}
    // @formatter:on

    public static void setFromLong(@Nullable MutableBlockPos pos, long l) {
        if (pos == null) {
            return;
        }

        final int x = (int) (l << 64 - BlockPos.X_SHIFT - BlockPos.NUM_X_BITS >> 64 - BlockPos.NUM_X_BITS);
        final int y = (int) (l << 64 - BlockPos.Y_SHIFT - BlockPos.NUM_Y_BITS >> 64 - BlockPos.NUM_Y_BITS);
        final int z = (int) (l << 64 - BlockPos.NUM_Z_BITS >> 64 - BlockPos.NUM_Z_BITS);

        pos.setPos(x, y, z);
    }
}
