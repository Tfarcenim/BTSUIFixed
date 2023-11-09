package io.karma.bts.common.util;

import java.awt.*;

public final class ColorUtils {
    private static final ThreadLocal<int[][]> iColorCache = ThreadLocal.withInitial(() -> new int[2][4]);
    private static final ThreadLocal<float[][]> fColorCache = ThreadLocal.withInitial(() -> new float[2][4]);

    // @formatter:off
    private ColorUtils() {}
    // @formatter:on

    public static void unpackRGBA(final int color, final int[] channels) {
        channels[0] = (color >> 24) & 0xFF;
        channels[1] = (color >> 16) & 0xFF;
        channels[2] = (color >> 8) & 0xFF;
        channels[3] = color & 0xFF;
    }

    public static int packRGBA(final int[] channels) { // @formatter:off
        return ((channels[0] & 0xFF) << 24)
            | ((channels[1] & 0xFF) << 16)
            | ((channels[2] & 0xFF) << 8)
            | (channels[3] & 0xFF);
    } // @formatter:on

    public static int lerpRGBA(final int colorA, final int colorB, float f) {
        if (f < 0F) {
            f = 0F;
        }
        if (f > 1F) {
            f = 1F;
        }

        final int[] aChannels = iColorCache.get()[0];
        unpackRGBA(colorA, aChannels);

        final float[] aComponents = fColorCache.get()[0];
        Color.RGBtoHSB(aChannels[0], aChannels[1], aChannels[2], aComponents);

        final int[] bChannels = iColorCache.get()[1];
        unpackRGBA(colorB, bChannels);

        final float[] bComponents = fColorCache.get()[1];
        Color.RGBtoHSB(bChannels[0], bChannels[1], bChannels[2], bComponents);

        float d = bComponents[0] - aComponents[0];

        if (aComponents[0] > bComponents[0]) {
            final float temp = bComponents[0];
            bComponents[0] = aComponents[0];
            aComponents[0] = temp;

            d = -d;
            f = 1F - f;
        }

        float h;

        if (d > 0.5F) {
            aComponents[0] = aComponents[0] + 1F;
            h = (aComponents[0] + f * (bComponents[0] - aComponents[0])) % 1F;
        }
        else {
            h = aComponents[0] + f * d;
        }

        final float s = aComponents[1] + (bComponents[1] - aComponents[1]) * f;
        final float b = aComponents[2] + (bComponents[2] - aComponents[2]) * f;
        final int a = (int) (aChannels[3] + (bChannels[3] - aChannels[3]) * f);
        final int rgb = Color.HSBtoRGB(h, s, b);

        return ((rgb & 0xFFFFFF) << 8) | (a & 0xFF);
    }
}
