package io.karma.bts.client.shader;

import io.karma.bts.client.shader.UniformBufferImpl.*;
import io.karma.bts.joml.*;
import io.karma.repackage.joml.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.function.BiFunction;

/**
 * A list of all possible uniform types supported
 * by the BTS shader API.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
public enum UniformType {
    // @formatter:off
    BOOL        (1,  (n, t) -> new BoolUniform(n)),
    BOOL_2      (2,  UniformType::createBoolArrayUniform),
    BOOL_3      (3,  UniformType::createBoolArrayUniform),
    BOOL_4      (4,  UniformType::createBoolArrayUniform),
    INT         (1,  (n, t) -> new IntUniform(n)),
    INT_2       (2,  UniformType::createIntArrayUniform),
    INT_3       (3,  UniformType::createIntArrayUniform),
    INT_4       (4,  UniformType::createIntArrayUniform),
    FLOAT       (1,  (n, t) -> new FloatUniform(n)),
    FLOAT_2     (2,  UniformType::createFloatArrayUniform),
    FLOAT_3     (3,  UniformType::createFloatArrayUniform),
    FLOAT_4     (4,  UniformType::createFloatArrayUniform),
    DOUBLE      (1,  (n, t) -> new DoubleUniform(n)),
    DOUBLE_2    (2,  UniformType::createDoubleArrayUniform),
    DOUBLE_3    (3,  UniformType::createDoubleArrayUniform),
    DOUBLE_4    (4,  UniformType::createDoubleArrayUniform),
    INT_V_2     (2,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector2i(0),
                        (b, p, v) -> GL20.glUniform2i(b.getLocation(n), v.x, v.y))),
    INT_V_3     (3,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector3i(0),
                        (b, p, v) -> GL20.glUniform3i(b.getLocation(n), v.x, v.y, v.z))),
    INT_V_4     (4,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector4i(0),
                        (b, p, v) -> GL20.glUniform4i(b.getLocation(n), v.x, v.y, v.z, v.w))),
    FLOAT_V_2   (2,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector2f(0F),
                        (b, p, v) -> GL20.glUniform2f(b.getLocation(n), v.x, v.y))),
    FLOAT_V_3   (3,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector3f(0F),
                        (b, p, v) -> GL20.glUniform3f(b.getLocation(n), v.x, v.y, v.z))),
    FLOAT_V_4   (4,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector4f(0F),
                        (b, p, v) -> GL20.glUniform4f(b.getLocation(n), v.x, v.y, v.z, v.w))),
    DOUBLE_V_2  (2,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector2d(0),
                        (b, p, v) -> GL40.glUniform2d(b.getLocation(n), v.x, v.y))),
    DOUBLE_V_3  (3,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector3d(0),
                        (b, p, v) -> GL40.glUniform3d(b.getLocation(n), v.x, v.y, v.z))),
    DOUBLE_V_4  (4,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Vector4d(0),
                        (b, p, v) -> GL40.glUniform4d(b.getLocation(n), v.x, v.y, v.z, v.w))),
    FLOAT_M_2   (4,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Matrix2f().identity(),
                        (b, p, m) -> {
                            final FloatBuffer buffer = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder()).asFloatBuffer();
                            GL20.glUniformMatrix2(b.getLocation(n), false, m.get(buffer));
                        })),
    FLOAT_M_3   (9,  (n, t) -> new GenericUniform<>(n, t,
                        () -> new Matrix3f().identity(),
                        (b, p, m) -> {
                            final FloatBuffer buffer = ByteBuffer.allocateDirect(36).order(ByteOrder.nativeOrder()).asFloatBuffer();
                            GL20.glUniformMatrix3(b.getLocation(n), false, m.get(buffer));
                        })),
    FLOAT_M_4   (16, (n, t) -> new GenericUniform<>(n, t,
                        () -> new Matrix4f().identity(),
                        (b, p, m) -> {
                            final FloatBuffer buffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asFloatBuffer();
                            GL20.glUniformMatrix4(b.getLocation(n), false, m.get(buffer));
                        }));
    // @formatter:on

    private final int size;
    private final BiFunction<String, UniformType, Uniform> uniformFactory;

    UniformType(final int size, final @NotNull BiFunction<String, UniformType, Uniform> uniformFactory) {
        this.size = size;
        this.uniformFactory = uniformFactory;
    }

    private static @NotNull GenericUniform<boolean[]> createBoolArrayUniform(final @NotNull String name, final @NotNull UniformType type) {
        return new GenericUniform<>(name, type, boolean[]::new, (b, p, a) -> {
            switch (a.length) {
                case 2:
                    GL20.glUniform2i(b.getLocation(name), a[0] ? 1 : 0, a[1] ? 1 : 0);
                    break;
                case 3:
                    GL20.glUniform3i(b.getLocation(name), a[1] ? 1 : 0, a[1] ? 1 : 0, a[2] ? 1 : 0);
                    break;
                case 4:
                    GL20.glUniform4i(b.getLocation(name), a[1] ? 1 : 0, a[1] ? 1 : 0, a[2] ? 1 : 0, a[3] ? 1 : 0);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid uniform size");
            }
        });
    }

    private static @NotNull GenericUniform<int[]> createIntArrayUniform(final @NotNull String name, final @NotNull UniformType type) {
        return new GenericUniform<>(name, type, int[]::new, (b, p, a) -> {
            switch (a.length) {
                case 2:
                    GL20.glUniform2i(b.getLocation(name), a[0], a[1]);
                    break;
                case 3:
                    GL20.glUniform3i(b.getLocation(name), a[1], a[1], a[2]);
                    break;
                case 4:
                    GL20.glUniform4i(b.getLocation(name), a[1], a[1], a[2], a[3]);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid uniform size");
            }
        });
    }

    private static @NotNull GenericUniform<float[]> createFloatArrayUniform(final @NotNull String name, final @NotNull UniformType type) {
        return new GenericUniform<>(name, type, float[]::new, (b, p, a) -> {
            switch (a.length) {
                case 2:
                    GL20.glUniform2f(b.getLocation(name), a[0], a[1]);
                    break;
                case 3:
                    GL20.glUniform3f(b.getLocation(name), a[1], a[1], a[2]);
                    break;
                case 4:
                    GL20.glUniform4f(b.getLocation(name), a[1], a[1], a[2], a[3]);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid uniform size");
            }
        });
    }

    private static @NotNull GenericUniform<double[]> createDoubleArrayUniform(final @NotNull String name, final @NotNull UniformType type) {
        return new GenericUniform<>(name, type, double[]::new, (b, p, a) -> {
            switch (a.length) {
                case 2:
                    GL40.glUniform2d(b.getLocation(name), a[0], a[1]);
                    break;
                case 3:
                    GL40.glUniform3d(b.getLocation(name), a[1], a[1], a[2]);
                    break;
                case 4:
                    GL40.glUniform4d(b.getLocation(name), a[1], a[1], a[2], a[3]);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid uniform size");
            }
        });
    }

    public int getSize() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public <U extends Uniform> @NotNull U createDefault(final @NotNull String name) {
        return (U) uniformFactory.apply(name, this);
    }
}
