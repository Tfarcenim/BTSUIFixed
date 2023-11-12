/*
 * Copyright 2022 Karma Krafts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.karma.kommons;

/**
 * Shared utility functions for common math operations
 * which didn't make it into Java's Math class.
 *
 * @author Alexander Hinze
 * @author Lorenz Klaus
 * @since 02/02/2022
 */
public final class MathUtils {
    public static final double TAU = Math.PI * 2D;

    //@formatter:off
    private MathUtils() {}
    //@formatter:on

    @SuppressWarnings("all")
    public static byte clamp(final byte value, final byte min, final byte max) {
        return value < min ? min : (value > max ? max : value);
    }

    @SuppressWarnings("all")
    public static short clamp(final short value, final short min, final short max) {
        return value < min ? min : (value > max ? max : value);
    }

    @SuppressWarnings("all")
    public static int clamp(final int value, final int min, final int max) {
        return value < min ? min : (value > max ? max : value);
    }

    @SuppressWarnings("all")
    public static long clamp(final long value, final long min, final long max) {
        return value < min ? min : (value > max ? max : value);
    }

    @SuppressWarnings("all")
    public static float clamp(final float value, final float min, final float max) {
        return value < min ? min : (value > max ? max : value);
    }

    @SuppressWarnings("all")
    public static double clamp(final double value, final double min, final double max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static byte lerp(final byte start, final byte end, final float factor) {
        return (byte) (start + (byte) ((float) (end - start) * factor));
    }

    public static short lerp(final short start, final short end, final float factor) {
        return (short) (start + (short) ((float) (end - start) * factor));
    }

    public static int lerp(final int start, final int end, final float factor) {
        return (start + (int) ((float) (end - start) * factor));
    }

    public static long lerp(final long start, final long end, final float factor) {
        return (start + (long) ((float) (end - start) * factor));
    }

    public static float lerp(final float start, final float end, final float factor) {
        return start + ((end - start) * factor);
    }

    public static double lerp(final double start, final double end, final float factor) {
        return start + (double) ((float) (end - start) * factor);
    }

    public static float mix(final float x, final float y, final float a) {
        return x * (1F - a) + y * a;
    }

    public static double mix(final double x, final double y, final float a) {
        return x * (1D - a) + y * a;
    }

    // @formatter:off
    public static float dot(final float x1, final float y1,
                            final float x2, final float y2) { // @formatter:on
        return (x1 * x2) + (y1 * y2);
    }

    // @formatter:off
    public static float dot(final float x1, final float y1, final float z1,
                            final float x2, final float y2, final float z2) { // @formatter:on
        return (x1 * x2) + (y1 * y2) + (z1 * z2);
    }

    // @formatter:off
    public static float dot(final float x1, final float y1, final float z1, final float w1,
                            final float x2, final float y2, final float z2, final float w2) { // @formatter:on
        return (x1 * x2) + (y1 * y2) + (z1 * z2) + (w1 * w2);
    }

    // @formatter:off
    public static double dot(final double x1, final double y1,
                             final double x2, final double y2) { // @formatter:on
        return (x1 * x2) + (y1 * y2);
    }

    // @formatter:off
    public static double dot(final double x1, final double y1, final double z1,
                             final double x2, final double y2, final double z2) { // @formatter:on
        return (x1 * x2) + (y1 * y2) + (z1 * z2);
    }

    // @formatter:off
    public static double dot(final double x1, final double y1, final double z1, final double w1,
                             final double x2, final double y2, final double z2, final double w2) { // @formatter:on
        return (x1 * x2) + (y1 * y2) + (z1 * z2) + (w1 * w2);
    }

    // @formatter:off
    public static float[] cross(final float x1, final float y1, final float z1,
                                final float x2, final float y2, final float z2) {
        return new float[] {
            (y1 * z2) - (y2 * z1),
            (z1 * x2) - (z2 * x1),
            (x1 * y2) - (x2 * y1)
        };
    }
    // @formatter:on

    /**
     * Determines the smallest multiple of the given values.
     *
     * @param values The values to calculate the smalles multiple of.
     * @return The smallest multiple of the given values.
     * @author Lorenz Klaus
     */
    public static int getSmallestMultiple(final int... values) {
        int base = 0;

        for (final int current : values) {
            if (current <= base) {
                continue;
            }

            base = current;
        }

        for (final int current : values) {
            if (base % current == 0) {
                continue;
            }

            base *= current;
        }

        return base;
    }
}
