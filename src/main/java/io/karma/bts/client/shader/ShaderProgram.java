package io.karma.bts.client.shader;

import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents an immutable GL shader program.
 * Provides functionality for specialization and uniforms.
 *
 * @author KitsuneAlex
 * @since 20/06/2022
 */
@SideOnly(Side.CLIENT)
public interface ShaderProgram extends ISelectiveResourceReloadListener {
    Consumer<UniformBuffer> DEFAULT_UNIFORM_CALLBACK = b -> { /* zzzZZZ */ };

    int getId();

    boolean isLinked();

    void requestRelink();

    boolean relinkIfNeeded();

    void use(final @NotNull Consumer<UniformBuffer> uniformCallback);

    default void use() {
        use(DEFAULT_UNIFORM_CALLBACK);
    }

    void release();

    @NotNull List<ShaderObject> getObjects();

    @Override
    default void onResourceManagerReload(final @NotNull IResourceManager manager, final @NotNull Predicate<IResourceType> filter) {
        if (filter.test(VanillaResourceType.SHADERS)) {
            getObjects().forEach(o -> o.onResourceManagerReload(manager, filter));
            requestRelink();
        }
    }
}
