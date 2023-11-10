package io.karma.bts.client.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public final class ShaderProgramBuilder {
    private static final ThreadLocal<ShaderProgramBuilder> INSTANCE = ThreadLocal.withInitial(ShaderProgramBuilder::new);
    private static final Consumer<ShaderProgram> DEFAULT_LINK_CALLBACK = p -> { /* zzzZZZ */ };
    private static final Consumer<UniformBuffer> DEFAULT_UNIFORM_CALLBACK = b -> { /* zzzZZZ */ };

    private final ArrayList<ShaderObject> objects = new ArrayList<>();
    private final HashMap<String, UniformType> uniforms = new HashMap<>();
    private Consumer<ShaderProgram> linkCallback = DEFAULT_LINK_CALLBACK;
    private Consumer<UniformBuffer> uniformCallback = DEFAULT_UNIFORM_CALLBACK;

    // @formatter:off
    private ShaderProgramBuilder() {}
    // @formatter:on

    public static @NotNull ShaderProgramBuilder getInstance() {
        return INSTANCE.get().reset();
    }

    public @NotNull ShaderProgramBuilder withObject(final @NotNull ShaderType type, final @NotNull Consumer<ShaderObjectBuilder> closure) {
        final ShaderObjectBuilder builder = ShaderObjectBuilder.create(type);
        closure.accept(builder);
        objects.add(builder.build());
        return this;
    }

    public @NotNull ShaderProgramBuilder withLinkCallback(final @NotNull Consumer<ShaderProgram> linkCallback) {
        this.linkCallback = linkCallback;
        return this;
    }

    public @NotNull ShaderProgramBuilder withStaticUniforms(final @NotNull Consumer<UniformBuffer> uniformCallback) {
        this.uniformCallback = uniformCallback;
        return this;
    }

    public @NotNull ShaderProgramBuilder withUniform(final @NotNull String name, final @NotNull UniformType type) {
        if (uniforms.containsKey(name)) {
            return this;
        }

        uniforms.put(name, type);
        return this;
    }

    private @NotNull ShaderProgramBuilder reset() {
        objects.clear();
        uniforms.clear();
        linkCallback = DEFAULT_LINK_CALLBACK;
        uniformCallback = DEFAULT_UNIFORM_CALLBACK;
        return this;
    }

    public @NotNull ShaderProgram build() {
        final ArrayList<ShaderObject> objects = new ArrayList<>(this.objects);
        final ShaderProgramImpl program = new ShaderProgramImpl(objects, linkCallback, uniformCallback, uniforms);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(program);
        program.relinkIfNeeded();
        return program;
    }

    private static final class ShaderProgramImpl implements ShaderProgram {
        private final int id;
        private final ArrayList<ShaderObject> objects;
        private final Consumer<ShaderProgram> linkCallback;
        private final Consumer<UniformBuffer> uniformCallback;

        private final UniformBufferImpl uniformBuffer;
        private boolean isLinked;
        private boolean isRelinkRequested = true;

        // @formatter:off
        public ShaderProgramImpl(final @NotNull ArrayList<ShaderObject> objects,
                                 final @NotNull Consumer<ShaderProgram> linkCallback,
                                 final @NotNull Consumer<UniformBuffer> uniformCallback,
                                 final @NotNull HashMap<String, UniformType> uniforms)
        { // @formatter:on
            id = GL20.glCreateProgram();

            if (id < 0) {
                throw new IllegalStateException("Could not create shader program");
            }

            this.objects = objects;
            this.linkCallback = linkCallback;
            this.uniformCallback = uniformCallback;
            uniformBuffer = new UniformBufferImpl(this);

            final Set<Entry<String, UniformType>> uniformEntries = uniforms.entrySet();

            for (final Entry<String, UniformType> entry : uniformEntries) {
                uniformBuffer.defineUniform(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public boolean isLinked() {
            return isLinked;
        }

        @Override
        public void use(final @NotNull Consumer<UniformBuffer> uniformCallback) {
            for (final ShaderObject obj : objects) {
                if (obj.recompileIfNeeded()) {
                    requestRelink();
                }
            }

            relinkIfNeeded();
            GL20.glUseProgram(id);
            uniformCallback.accept(uniformBuffer);
        }

        @Override
        public void release() {
            GL20.glUseProgram(0);
        }

        @Override
        public void requestRelink() {
            isRelinkRequested = true;
        }

        @Override
        public boolean relinkIfNeeded() {
            if (!isRelinkRequested) {
                return false;
            }

            if (isLinked) {
                objects.forEach(o -> GL20.glDetachShader(id, o.getId()));
                isLinked = false;
            }

            objects.forEach(o -> GL20.glAttachShader(id, o.getId()));
            GL20.glLinkProgram(id);

            if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
                final int length = GL20.glGetShaderi(id, GL20.GL_INFO_LOG_LENGTH);
                final String log = GL20.glGetShaderInfoLog(id, length);
              //  throw new IllegalStateException(log);
            }

            uniformBuffer.updateLocations();
            isLinked = true;
            uniformCallback.accept(uniformBuffer);
            linkCallback.accept(this);
            return true;
        }

        @Override
        public @NotNull List<ShaderObject> getObjects() {
            return objects;
        }
    }
}
