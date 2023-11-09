package io.karma.bts.client.shader;

import io.karma.bts.client.shader.prepro.ShaderPreProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Public API for creating shader objects.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
@SideOnly(Side.CLIENT)
public final class ShaderObjectBuilder {
    private static final ThreadLocal<ShaderObjectBuilder> INSTANCE = ThreadLocal.withInitial(ShaderObjectBuilder::new);
    private static final Consumer<Throwable> DEFAULT_ERROR_HANDLER = t -> { /* discard */ };
    private static final Consumer<ShaderObject> DEFAULT_COMPILE_CALLBACK = o -> { /* zzZZZ */ };

    private ShaderType type;
    private Consumer<Throwable> errorHandler = DEFAULT_ERROR_HANDLER;
    private Supplier<String> sourceProvider;
    private Consumer<ShaderObject> compileCallback = DEFAULT_COMPILE_CALLBACK;

    // @formatter:off
    private ShaderObjectBuilder() {}
    // @formatter:on

    public static @NotNull ShaderObjectBuilder create(final @NotNull ShaderType type) {
        final ShaderObjectBuilder builder = INSTANCE.get().reset();
        builder.type = type;
        return builder;
    }

    public @NotNull ShaderObjectBuilder withErrorHandler(final @NotNull Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public @NotNull ShaderObjectBuilder withCompileCallback(final @NotNull Consumer<ShaderObject> compileCallback) {
        this.compileCallback = compileCallback;
        return this;
    }

    public @NotNull ShaderObjectBuilder fromSource(final @NotNull String source) {
        sourceProvider = () -> source;
        return this;
    }

    public @NotNull ShaderObjectBuilder fromResource(final @NotNull ResourceLocation location) {
        sourceProvider = () -> {
            final String path = String.format("/assets/%s/%s", location.getNamespace(), location.getPath());

            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ShaderObjectBuilder.class.getResourceAsStream(path))))) {
                final StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    // @formatter:off
                    builder.append(line).append('\n');
                    // @formatter:on
                }

                return builder.toString();
            }
            catch (Throwable t) {
                errorHandler.accept(t);
                return "";
            }
        };

        return this;
    }

    private @NotNull ShaderObjectBuilder reset() {
        type = null;
        sourceProvider = null;
        errorHandler = DEFAULT_ERROR_HANDLER;
        return this;
    }

    public @NotNull ShaderObject build() {
        if (type == null || sourceProvider == null) {
            throw new IllegalStateException("Missing required property");
        }

        final ShaderObjectImpl object = new ShaderObjectImpl(type, sourceProvider, compileCallback);
        object.recompileIfNeeded();
        return object;
    }

    /**
     * Non-public implementation of {@link ShaderObject} that's
     * built for easy hot-reloading of resources like required by the game.
     *
     * @author KitsuneAlex
     * @since 21/06/2022
     */
    private static final class ShaderObjectImpl implements ShaderObject {
        private final ShaderType type;
        private final Supplier<String> sourceProvider;
        private final int id;
        private final Consumer<ShaderObject> compileCallback;
        private ShaderProgram program;
        private boolean isCompiled;
        private boolean isRecompileRequested = true;

        // @formatter:off
        public ShaderObjectImpl(final @NotNull ShaderType type,
                                final @NotNull Supplier<String> sourceProvider,
                                final @NotNull Consumer<ShaderObject> compileCallback
        ) { // @formatter:on
            this.type = type;
            this.sourceProvider = sourceProvider;
            this.compileCallback = compileCallback;
            id = GL20.glCreateShader(type.getInternalType());

            if (id < 0) {
                throw new IllegalStateException("Could not create shader object");
            }
        }

        @Override
        public @NotNull ShaderType getType() {
            return type;
        }

        @Override
        public @NotNull String getSource() {
            return sourceProvider.get();
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public boolean isCompiled() {
            return isCompiled;
        }

        @Override
        public void requestRecompile() {
            isRecompileRequested = true;
        }

        @Override
        public boolean recompileIfNeeded() {
            if (!isRecompileRequested) {
                return false;
            }

            isCompiled = false;
            final String source = ShaderPreProcessor.getInstance().process(getSource());
            GL20.glShaderSource(id, source);
            GL20.glCompileShader(id);

            if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
                final int length = GL20.glGetShaderi(id, GL20.GL_INFO_LOG_LENGTH);
                final String log = GL20.glGetShaderInfoLog(id, length);
                throw new IllegalStateException(String.format("Shader log: %s\nShader source:\n%s", log, source));
            }

            isCompiled = true;
            compileCallback.accept(this);
            isRecompileRequested = false;
            return true;
        }

        @Override
        public @NotNull ShaderProgram getProgram() {
            return program;
        }

        @Override
        public void setProgram(final @NotNull ShaderProgram program) {
            if (this.program != null) {
                throw new IllegalStateException("Program already linked");
            }

            this.program = program;
        }
    }
}