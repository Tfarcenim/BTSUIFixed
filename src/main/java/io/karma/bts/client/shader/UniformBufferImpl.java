package io.karma.bts.client.shader;

import io.karma.bts.joml.*;
import io.karma.repackage.joml.*;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * A simple, caching instance-based uniform buffer.
 * This will only make GL calls when actually needed.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
@SideOnly(Side.CLIENT)
public final class UniformBufferImpl implements UniformBuffer {
    private final ShaderProgram program;
    private final HashMap<String, Uniform> uniforms = new HashMap<>();
    private final Object2IntOpenHashMap<String> uniformLocations = new Object2IntOpenHashMap<>();

    UniformBufferImpl(final @NotNull ShaderProgram program) {
        this.program = program;
        uniformLocations.defaultReturnValue(-1);
    }

    @Override
    public void defineUniform(final @NotNull String name, final @NotNull UniformType type) {
        if (uniforms.containsKey(name)) {
            throw new IllegalStateException(String.format("Uniform %s is already defined", name));
        }

        final Uniform uniform = type.createDefault(name);
        uniform.setBuffer(this);
        uniforms.put(name, uniform);
        uniformLocations.put(name, GL20.glGetUniformLocation(program.getId(), name));
    }

    @Override
    public int getLocation(final @NotNull String name) {
        return uniformLocations.getInt(name);
    }

    @Override
    public void updateLocations() {
        uniformLocations.clear();
        final Collection<Uniform> uniforms = this.uniforms.values();
        final int program = this.program.getId();

        for (final Uniform uniform : uniforms) {
            final String name = uniform.getName();
            uniformLocations.put(name, GL20.glGetUniformLocation(program, name));
        }
    }

    @Override
    public void set1b(final @NotNull String name, final boolean b) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.BOOL) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final BoolUniform boolUniform = (BoolUniform) uniform;

        if (boolUniform.value == b) {
            return;
        }

        boolUniform.value = b;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set2b(final @NotNull String name, final boolean b1, final boolean b2) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.BOOL_2) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<boolean[]> boolUniform = (GenericUniform<boolean[]>) uniform;
        final boolean[] value = boolUniform.value;

        if (value[0] == b1 && value[1] == b2) {
            return;
        }

        value[0] = b1;
        value[1] = b2;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set3b(final @NotNull String name, final boolean b1, final boolean b2, final boolean b3) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.BOOL_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<boolean[]> boolUniform = (GenericUniform<boolean[]>) uniform;
        final boolean[] value = boolUniform.value;

        if (value[0] == b1 && value[1] == b2 && value[2] == b3) {
            return;
        }

        value[0] = b1;
        value[1] = b2;
        value[2] = b3;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set4b(final @NotNull String name, final boolean b1, final boolean b2, final boolean b3, final boolean b4) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.BOOL_4) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<boolean[]> boolUniform = (GenericUniform<boolean[]>) uniform;
        final boolean[] value = boolUniform.value;

        if (value[0] == b1 && value[1] == b2 && value[2] == b3 && value[3] == b4) {
            return;
        }

        value[0] = b1;
        value[1] = b2;
        value[2] = b3;
        value[3] = b4;
        uniform.apply(program);
    }

    @Override
    public void set1i(@NotNull String name, int i) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.INT) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final IntUniform intUniform = (IntUniform) uniform;

        if (intUniform.value == i) {
            return;
        }

        intUniform.value = i;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set2i(final @NotNull String name, final int i1, final int i2) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.INT_2) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<int[]> boolUniform = (GenericUniform<int[]>) uniform;
        final int[] value = boolUniform.value;

        if (value[0] == i1 && value[1] == i2) {
            return;
        }

        value[0] = i1;
        value[1] = i2;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set3i(final @NotNull String name, final int i1, final int i2, final int i3) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.INT_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<int[]> boolUniform = (GenericUniform<int[]>) uniform;
        final int[] value = boolUniform.value;

        if (value[0] == i1 && value[1] == i2 && value[2] == i3) {
            return;
        }

        value[0] = i1;
        value[1] = i2;
        value[2] = i3;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set4i(final @NotNull String name, final int i1, final int i2, final int i3, final int i4) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.INT_4) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<int[]> boolUniform = (GenericUniform<int[]>) uniform;
        final int[] value = boolUniform.value;

        if (value[0] == i1 && value[1] == i2 && value[2] == i3 && value[3] == i4) {
            return;
        }

        value[0] = i1;
        value[1] = i2;
        value[2] = i3;
        value[3] = i4;
        uniform.apply(program);
    }

    @Override
    public void set1f(@NotNull String name, float f) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT) {
            throw new IllegalStateException("Invalid uniform type");
        }

        ((FloatUniform) uniform).value = f;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set2f(final @NotNull String name, final float f1, final float f2) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_2) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<float[]> boolUniform = (GenericUniform<float[]>) uniform;
        final float[] value = boolUniform.value;

        if (value[0] == f1 && value[1] == f2) {
            return;
        }

        value[0] = f1;
        value[1] = f2;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set3f(final @NotNull String name, final float f1, final float f2, final float f3) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<float[]> boolUniform = (GenericUniform<float[]>) uniform;
        final float[] value = boolUniform.value;

        if (value[0] == f1 && value[1] == f2 && value[2] == f3) {
            return;
        }

        value[0] = f1;
        value[1] = f2;
        value[2] = f3;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set4f(final @NotNull String name, final float f1, final float f2, final float f3, final float f4) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<float[]> boolUniform = (GenericUniform<float[]>) uniform;
        final float[] value = boolUniform.value;

        if (value[0] == f1 && value[1] == f2 && value[2] == f3 && value[3] == f4) {
            return;
        }

        value[0] = f1;
        value[1] = f2;
        value[2] = f3;
        value[3] = f4;
        uniform.apply(program);
    }

    @Override
    public void set1d(@NotNull String name, double d) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.DOUBLE) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final DoubleUniform doubleUniform = (DoubleUniform) uniform;

        if (doubleUniform.value == d) {
            return;
        }

        doubleUniform.value = d;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set2d(final @NotNull String name, final double d1, final double d2) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.DOUBLE_2) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<double[]> boolUniform = (GenericUniform<double[]>) uniform;
        final double[] value = boolUniform.value;

        if (value[0] == d1 && value[1] == d2) {
            return;
        }

        value[0] = d1;
        value[1] = d2;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set3d(final @NotNull String name, final double d1, final double d2, final double d3) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.DOUBLE_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<double[]> boolUniform = (GenericUniform<double[]>) uniform;
        final double[] value = boolUniform.value;

        if (value[0] == d1 && value[1] == d2 && value[2] == d3) {
            return;
        }

        value[0] = d1;
        value[1] = d2;
        value[2] = d3;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set4d(final @NotNull String name, final double d1, final double d2, final double d3, final double d4) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.DOUBLE_4) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<double[]> boolUniform = (GenericUniform<double[]>) uniform;
        final double[] value = boolUniform.value;

        if (value[0] == d1 && value[1] == d2 && value[2] == d3 && value[3] == d4) {
            return;
        }

        value[0] = d1;
        value[1] = d2;
        value[2] = d3;
        value[3] = d4;
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set2vi(final @NotNull String name, final @NotNull Vector2i v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.INT_V_2) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector2i> vecUniform = (GenericUniform<Vector2i>) uniform;
        final Vector2i vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set3vi(final @NotNull String name, final @NotNull Vector3i v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.INT_V_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector3i> vecUniform = (GenericUniform<Vector3i>) uniform;
        final Vector3i vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set4vi(final @NotNull String name, final @NotNull Vector4i v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.INT_V_4) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector4i> vecUniform = (GenericUniform<Vector4i>) uniform;
        final Vector4i vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set2vf(final @NotNull String name, final @NotNull Vector2f v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_V_2) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector2f> vecUniform = (GenericUniform<Vector2f>) uniform;
        final Vector2f vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set3vf(final @NotNull String name, final @NotNull Vector3f v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_V_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector3f> vecUniform = (GenericUniform<Vector3f>) uniform;
        final Vector3f vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set4vf(final @NotNull String name, final @NotNull Vector4f v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_V_4) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector4f> vecUniform = (GenericUniform<Vector4f>) uniform;
        final Vector4f vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set2vf64(final @NotNull String name, final @NotNull Vector2d v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.DOUBLE_V_2) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector2d> vecUniform = (GenericUniform<Vector2d>) uniform;
        final Vector2d vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set3vf64(final @NotNull String name, final @NotNull Vector3d v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.DOUBLE_V_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector3d> vecUniform = (GenericUniform<Vector3d>) uniform;
        final Vector3d vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set4vf64(final @NotNull String name, final @NotNull Vector4d v) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.DOUBLE_V_4) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Vector4d> vecUniform = (GenericUniform<Vector4d>) uniform;
        final Vector4d vector = vecUniform.value;

        if (vector.equals(v)) {
            return;
        }

        vector.set(v);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set2x2mf(final @NotNull String name, final @NotNull Matrix2f m) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_M_2) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Matrix2f> vecUniform = (GenericUniform<Matrix2f>) uniform;
        final Matrix2f vector = vecUniform.value;

        if (vector.equals(m)) {
            return;
        }

        vector.set(m);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set3x3mf(final @NotNull String name, final @NotNull Matrix3f m) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_M_3) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Matrix3f> vecUniform = (GenericUniform<Matrix3f>) uniform;
        final Matrix3f vector = vecUniform.value;

        if (vector.equals(m)) {
            return;
        }

        vector.set(m);
        uniform.apply(program);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set4x4mf(final @NotNull String name, final @NotNull Matrix4f m) {
        final Uniform uniform = uniforms.get(name);

        if (uniform == null) {
            throw new IllegalStateException("No such uniform");
        }

        if (uniform.getType() != UniformType.FLOAT_M_4) {
            throw new IllegalStateException("Invalid uniform type");
        }

        final GenericUniform<Matrix4f> vecUniform = (GenericUniform<Matrix4f>) uniform;
        final Matrix4f vector = vecUniform.value;

        if (vector.equals(m)) {
            return;
        }

        vector.set(m);
        uniform.apply(program);
    }

    @Override
    public @NotNull Set<String> getUniforms() {
        return uniforms.keySet();
    }

    public static abstract class Uniform {
        protected final String name;
        protected UniformBufferImpl buffer;

        public Uniform(final @NotNull String name) {
            this.name = name;
        }

        public @NotNull UniformBufferImpl getBuffer() {
            return buffer;
        }

        public void setBuffer(final @NotNull UniformBufferImpl buffer) {
            this.buffer = buffer;
        }

        public @NotNull String getName() {
            return name;
        }

        public abstract void apply(final @NotNull ShaderProgram program);

        public abstract @NotNull UniformType getType();
    }

    public static final class BoolUniform extends Uniform {
        public boolean value;

        public BoolUniform(final @NotNull String name) {
            super(name);
        }

        @Override
        public void apply(final @NotNull ShaderProgram program) {
            GL20.glUniform1i(buffer.getLocation(name), value ? 1 : 0);
        }

        @Override
        public @NotNull UniformType getType() {
            return UniformType.BOOL;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (!(obj instanceof BoolUniform)) {
                return false;
            }

            final BoolUniform other = (BoolUniform) obj;
            return other.name.equals(name) && other.value == value;
        }

        @Override
        public @NotNull String toString() {
            return String.format("%s=%b", name, value);
        }
    }

    public static final class IntUniform extends Uniform {
        public int value;

        public IntUniform(final @NotNull String name) {
            super(name);
        }

        @Override
        public void apply(final @NotNull ShaderProgram program) {
            GL20.glUniform1i(buffer.getLocation(name), value);
        }

        @Override
        public @NotNull UniformType getType() {
            return UniformType.INT;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (!(obj instanceof IntUniform)) {
                return false;
            }

            final IntUniform other = (IntUniform) obj;
            return other.name.equals(name) && other.value == value;
        }

        @Override
        public @NotNull String toString() {
            return String.format("%s=%d", name, value);
        }
    }

    public static final class FloatUniform extends Uniform {
        public float value;

        public FloatUniform(final @NotNull String name) {
            super(name);
        }

        @Override
        public void apply(final @NotNull ShaderProgram program) {
            GL20.glUniform1f(buffer.getLocation(name), value);
        }

        @Override
        public @NotNull UniformType getType() {
            return UniformType.FLOAT;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (!(obj instanceof FloatUniform)) {
                return false;
            }

            final FloatUniform other = (FloatUniform) obj;
            return other.name.equals(name) && other.value == value;
        }

        @Override
        public @NotNull String toString() {
            return String.format("%s=%f", name, value);
        }
    }

    public static final class DoubleUniform extends Uniform {
        public double value;

        public DoubleUniform(final @NotNull String name) {
            super(name);
        }

        @Override
        public void apply(final @NotNull ShaderProgram program) {
            GL40.glUniform1d(buffer.getLocation(name), value);
        }

        @Override
        public @NotNull UniformType getType() {
            return UniformType.DOUBLE;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (!(obj instanceof DoubleUniform)) {
                return false;
            }

            final DoubleUniform other = (DoubleUniform) obj;
            return other.name.equals(name) && other.value == value;
        }

        @Override
        public @NotNull String toString() {
            return String.format("%s=%f", name, value);
        }
    }

    public static final class GenericUniform<T> extends Uniform {
        public final T value;
        private final UniformType type;
        private final TriConsumer<UniformBufferImpl, ShaderProgram, T> function;

        // @formatter:off
        public GenericUniform(final @NotNull String name,
                              final @NotNull UniformType type,
                              final @NotNull Supplier<T> initializer,
                              final @NotNull TriConsumer<UniformBufferImpl, ShaderProgram, T> function
        ) { // @formatter:on
            super(name);
            this.type = type;
            this.function = function;
            value = initializer.get();
        }

        // @formatter:off
        public GenericUniform(final @NotNull String name,
                              final @NotNull UniformType type,
                              final @NotNull IntFunction<T> initializer,
                              final @NotNull TriConsumer<UniformBufferImpl, ShaderProgram, T> function
        ) { // @formatter:on
            this(name, type, () -> initializer.apply(type.getSize()), function);
        }

        @Override
        public void apply(final @NotNull ShaderProgram program) {
            function.accept(buffer, program, value);
        }

        @Override
        public @NotNull UniformType getType() {
            return type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        @Override
        public boolean equals(final @Nullable Object obj) {
            if (!(obj instanceof GenericUniform)) {
                return false;
            }

            final GenericUniform<?> other = (GenericUniform<?>) obj;
            return other.name.equals(name) && other.value.equals(value);
        }

        @Override
        public @NotNull String toString() {
            return String.format("%s=%s", name, value.toString());
        }
    }
}
