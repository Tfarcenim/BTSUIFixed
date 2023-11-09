package io.karma.bts.client.shader.prepro;

import io.karma.bts.common.BTSConstants;
import io.karma.kommons.collection.GenericMap;
import io.karma.kommons.function.TriConsumer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * All additional pre-processor directives provided
 * by the BTS shader pre-processor.
 *
 * @author KitsuneAlex
 * @since 21/06/2022
 */
@SideOnly(Side.CLIENT)
public enum BuiltInMacro implements Macro {
    // @formatter:off
    INCLUDE("include", BuiltInMacro::transformInclude);
    // @formatter:on

    private final String friendlyName;
    private final TriConsumer<GenericMap<String>, String[], StringBuilder> transform;

    BuiltInMacro(final @NotNull String friendlyName, final @NotNull TriConsumer<GenericMap<String>, String[], StringBuilder> transform) {
        this.friendlyName = friendlyName;
        this.transform = transform;
    }

    private static void transformInclude(final @NotNull GenericMap<String> ctx, final @NotNull String[] args, final @NotNull StringBuilder builder) throws PreProcessorException {
        final int numArgs = args.length;

        if (numArgs != 2) {
            throw new PreProcessorException("Invalid number of macro arguments: %d", numArgs);
        }

        final String rawPath = args[1].replace("\"", "");
        final String path = String.format("/assets/%s/%s", BTSConstants.MODID, rawPath);
        final StringBuilder tempBuilder = new StringBuilder();

        try (final InputStream stream = Objects.requireNonNull(BuiltInMacro.class.getResourceAsStream(path))) {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    // @formatter:off
                    tempBuilder.append(line).append('\n');
                    // @formatter:on
                }
            }
        }
        catch (Throwable t) {
            // Re-throw as pre-pro exception
            throw new PreProcessorException("Could not load include from %s", args[0]);
        }

        tempBuilder.trimToSize();
        builder.append(ShaderPreProcessor.getInstance().process(tempBuilder.toString()));
    }

    @Override
    public @NotNull String getName() {
        return friendlyName;
    }

    @Override
    public void transform(final @NotNull GenericMap<String> ctx, final @NotNull String[] args, final @NotNull StringBuilder builder) throws PreProcessorException {
        transform.accept(ctx, args, builder);
    }
}
