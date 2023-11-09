package io.karma.bts.common.mixins;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.DirectoryDiscoverer;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

/**
 * @author Alexander Hinze
 * @since 30/07/2022
 */
@Mixin(value = DirectoryDiscoverer.class, remap = false)
public final class DirectoryDiscovererMixin {
    @Unique
    private Level previousLevel;

    @Inject(method = "exploreFileSystem", at = @At("HEAD"))
    private void onExploreFileSystemPre(final @NotNull String path, final @NotNull File modDir, final @NotNull List<ModContainer> harvestedMods, final @NotNull ModCandidate candidate, final @Nullable MetadataCollection mc, final @NotNull CallbackInfo cbi) {
        final Logger logger = ((Logger) FMLLog.log);
        previousLevel = logger.getLevel();
        logger.setLevel(Level.FATAL);
    }

    @Inject(method = "exploreFileSystem", at = @At("TAIL"))
    private void onExploreFileSystemPost(final @NotNull String path, final @NotNull File modDir, final @NotNull List<ModContainer> harvestedMods, final @NotNull ModCandidate candidate, final @Nullable MetadataCollection mc, final @NotNull CallbackInfo cbi) {
        ((Logger) FMLLog.log).setLevel(previousLevel);
    }

    @Inject(method = "exploreFileSystem", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", shift = Shift.BEFORE, by = 1), cancellable = true)
    private void onExploreFileSystemError(final @NotNull String path, final @NotNull File modDir, final @NotNull List<ModContainer> harvestedMods, final @NotNull ModCandidate candidate, final @Nullable MetadataCollection mc, final @NotNull CallbackInfo cbi) {
        cbi.cancel(); // Cancel outputting these errors, since they don't do us any good
        // We can't stop them either tho, because of Java 9+ compatibility with OW2 ASM.
    }
}
