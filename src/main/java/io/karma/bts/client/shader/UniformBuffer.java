package io.karma.bts.client.shader;

import io.karma.bts.joml.*;
import io.karma.repackage.joml.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Provides access to a shader object's uniform variables.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
@SideOnly(Side.CLIENT)
public interface UniformBuffer { // @formatter:off
    void defineUniform(final @NotNull String name, final @NotNull UniformType type);

    int getLocation(final @NotNull String name);

    void updateLocations();

    @NotNull Set<String> getUniforms();

    void set1b(final @NotNull String name, final boolean b);
    void set2b(final @NotNull String name, final boolean b1, final boolean b2);
    void set3b(final @NotNull String name, final boolean b1, final boolean b2, final boolean b3);
    void set4b(final @NotNull String name, final boolean b1, final boolean b2, final boolean b3, final boolean b4);

    void set1i(final @NotNull String name, final int i);
    void set2i(final @NotNull String name, final int i1, final int i2);
    void set3i(final @NotNull String name, final int i1, final int i2, final int i3);
    void set4i(final @NotNull String name, final int i1, final int i2, final int i3, final int i4);

    void set1f(final @NotNull String name, final float f);
    void set2f(final @NotNull String name, final float f1, final float f2);
    void set3f(final @NotNull String name, final float f1, final float f2, final float f3);
    void set4f(final @NotNull String name, final float f1, final float f2, final float f3, final float f4);

    void set1d(final @NotNull String name, final double d);
    void set2d(final @NotNull String name, final double d1, final double d2);
    void set3d(final @NotNull String name, final double d1, final double d2, final double d3);
    void set4d(final @NotNull String name, final double d1, final double d2, final double d3, final double d4);

    void set2vi(final @NotNull String name, final @NotNull Vector2i v);
    void set3vi(final @NotNull String name, final @NotNull Vector3i v);
    void set4vi(final @NotNull String name, final @NotNull Vector4i v);

    void set2vf(final @NotNull String name, final @NotNull Vector2f v);
    void set3vf(final @NotNull String name, final @NotNull Vector3f v);
    void set4vf(final @NotNull String name, final @NotNull Vector4f v);

    void set2vf64(final @NotNull String name, final @NotNull Vector2d v);
    void set3vf64(final @NotNull String name, final @NotNull Vector3d v);
    void set4vf64(final @NotNull String name, final @NotNull Vector4d v);

    void set2x2mf(final @NotNull String name, final @NotNull Matrix2f m);
    void set3x3mf(final @NotNull String name, final @NotNull Matrix3f m);
    void set4x4mf(final @NotNull String name, final @NotNull Matrix4f m);
} // @formatter:on
