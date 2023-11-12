package io.karma.bts.client.shader.prepro;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a pre-processor macro in GLSL.
 * Defines its name and functionality through
 * a String transformation.
 *
 * @author KitsuneAlex
 * @since 21/06/2022
 */
@SideOnly(Side.CLIENT)
public interface Macro {
    @NotNull String getName();

    void transform(final @NotNull Map<String,Object> ctx, final @NotNull String[] args, final @NotNull StringBuilder builder) throws PreProcessorException;
}
