package io.karma.bts.common.mixins;

import io.karma.bts.common.BTSConstants;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * @author Alexander Hinze
 * @since 30/07/2022
 */
public final class MixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(final @NotNull String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final @NotNull String targetClassName, final @NotNull String mixinClassName) {
        if (BTSConstants.ENABLE_CL_WARNINGS) {
            // @formatter:off
            return !mixinClassName.equals("io.karma.io.karma.bts.common.mixins.JarDiscovererMixin")
                && !mixinClassName.equals("io.karma.io.karma.bts.common.mixins.DirectoryDiscovererMixin");
            // @formatter:on
        }

        return true;
    }

    @Override
    public void acceptTargets(final @NotNull Set<String> myTargets, final @NotNull Set<String> otherTargets) {

    }

    @Override
    public @NotNull List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final @NotNull String targetClassName, final @NotNull ClassNode targetClass, final @NotNull String mixinClassName, final @NotNull IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(final @NotNull String targetClassName, final @NotNull ClassNode targetClass, final @NotNull String mixinClassName, final @NotNull IMixinInfo mixinInfo) {

    }
}
