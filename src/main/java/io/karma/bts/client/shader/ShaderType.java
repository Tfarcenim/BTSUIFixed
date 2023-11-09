package io.karma.bts.client.shader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.*;

import java.util.function.BooleanSupplier;

/**
 * All possible shader object types provided by OpenGL
 * up to version 4.3.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
@SideOnly(Side.CLIENT)
public enum ShaderType {
    // @formatter:off
    VERTEX      (GL20.GL_VERTEX_SHADER,          ShaderType::hasVertexSupport),
    FRAGMENT    (GL20.GL_FRAGMENT_SHADER,        ShaderType::hasFragmentSupport),
    GEOMETRY    (GL32.GL_GEOMETRY_SHADER,        ShaderType::hasGeometrySupport),
    TESS_CTRL   (GL40.GL_TESS_CONTROL_SHADER,    ShaderType::hasTessellationSupport),
    TESS_EVAL   (GL40.GL_TESS_EVALUATION_SHADER, ShaderType::hasTessellationSupport),
    COMPUTE     (GL43.GL_COMPUTE_SHADER,         ShaderType::hasComputeSupport);
    // @formatter:on

    private final int internalType;
    private final BooleanSupplier checker;

    ShaderType(final int internalType, final @NotNull BooleanSupplier checker) {
        this.internalType = internalType;
        this.checker = checker;
    }

    private static boolean hasVertexSupport() {
        return GLContext.getCapabilities().GL_ARB_vertex_shader;
    }

    private static boolean hasFragmentSupport() {
        return GLContext.getCapabilities().GL_ARB_fragment_shader;
    }

    private static boolean hasGeometrySupport() {
        return GLContext.getCapabilities().GL_ARB_geometry_shader4;
    }

    private static boolean hasTessellationSupport() {
        return GLContext.getCapabilities().GL_ARB_tessellation_shader;
    }

    private static boolean hasComputeSupport() {
        return GLContext.getCapabilities().GL_ARB_compute_shader;
    }

    public int getInternalType() {
        return internalType;
    }

    public boolean isSupported() {
        return checker.getAsBoolean();
    }
}
