package io.karma.bts.client.shader.prepro;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * A very primitive implementation of a GLSL pre-processor
 * for extending the language with things like includes.
 *
 * @author KitsuneAlex
 * @since 21/06/2022
 */
@SideOnly(Side.CLIENT)
public final class ShaderPreProcessor {
    static final Logger LOGGER = LogManager.getLogger(ShaderPreProcessor.class);
    private static final ThreadLocal<ShaderPreProcessor> INSTANCE = ThreadLocal.withInitial(ShaderPreProcessor::new);
    private static final HashMap<String, Macro> MACROS = new HashMap<>();

    static {
        addMacro(BuiltInMacro.INCLUDE);
    }

    private final HashMap<String,Object> context = new HashMap<>();

    // @formatter:off
    private ShaderPreProcessor() {}
    // @formatter:on

    public static @NotNull ShaderPreProcessor getInstance() {
        return INSTANCE.get();
    }

    public static void addMacro(final @NotNull Macro macro) {
        final String name = macro.getName();

        if (MACROS.containsKey(name)) {
            throw new IllegalStateException(String.format("Macro %s already exists", name));
        }

        MACROS.put(name, macro);
    }

    public static void removeMacro(final @NotNull Macro macro) {
        removeMacro(macro.getName());
    }

    public static void removeMacro(final @NotNull String name) {
        if (!MACROS.containsKey(name)) {
            throw new IllegalStateException(String.format("No macro named %s", name));
        }

        MACROS.remove(name);
    }

    public @NotNull String process(final @NotNull String source) throws PreProcessorException {
        final String[] lines = source.split("\n");
        final StringBuilder builder = new StringBuilder();

        context.clear();

        for (final String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty()) {
                continue; // Skip empty lines completely
            }

            while (trimmedLine.charAt(0) == ' ') {
                trimmedLine = trimmedLine.substring(1);
            }

            if (trimmedLine.equals("\n")) {
                continue; // Skip empty lines completely
            }

            if (trimmedLine.startsWith("#")) {
                final int firstWs = trimmedLine.indexOf(' ');

                if (firstWs > 1) {
                    final String macroName = trimmedLine.substring(1, firstWs);

                    if (MACROS.containsKey(macroName)) {
                        final String[] args = trimmedLine.split("\\s+");
                        MACROS.get(macroName).transform(context, args, builder);
                        continue; // Eat up the pre-processor directive
                    }
                }
            }

            // @formatter:off
            builder.append(line).append('\n');
            // @formatter:on
        }

        builder.trimToSize();
        return builder.toString();
    }
}
