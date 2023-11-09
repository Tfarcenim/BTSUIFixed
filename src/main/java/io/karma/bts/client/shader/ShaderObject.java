package io.karma.bts.client.shader;

import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Represents an immutable GL shader object.
 * Provides functionality for specialization and uniforms.
 * For creating shader objects, see {@link ShaderObjectBuilder}.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
@SideOnly(Side.CLIENT)
public interface ShaderObject extends ISelectiveResourceReloadListener {
    @NotNull ShaderType getType();

    @NotNull String getSource();

    int getId();

    boolean recompileIfNeeded();

    void requestRecompile();

    boolean isCompiled();

    @NotNull ShaderProgram getProgram();

    void setProgram(final @NotNull ShaderProgram program);

    @Override
    default void onResourceManagerReload(final @NotNull IResourceManager manager, final @NotNull Predicate<IResourceType> filter) {
        if (filter.test(VanillaResourceType.SHADERS)) {
            requestRecompile();
        }
    }
}
