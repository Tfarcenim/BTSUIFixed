package io.karma.bts.common.mixins;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.JarDiscoverer;
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

import java.util.List;
import java.util.jar.JarFile;

/**
 * @author Alexander Hinze
 * @since 30/07/2022
 */
@Mixin(value = JarDiscoverer.class, remap = false)
public final class JarDiscovererMixin {
    @Unique
    private Level previousLevel;

    @Inject(method = "findClassesASM", at = @At("HEAD"))
    private void onFindClassesASMPre(final @NotNull ModCandidate candidate, final @NotNull ASMDataTable table, final @NotNull JarFile jar, final @NotNull List<ModContainer> foundMods, final @NotNull MetadataCollection mc, final @NotNull CallbackInfo cbi) {
        final Logger logger = ((Logger) FMLLog.log);
        previousLevel = logger.getLevel();
        logger.setLevel(Level.FATAL);
    }

    @Inject(method = "findClassesASM", at = @At(value = "TAIL"))
    private void onFindClassesASMPost(final @NotNull ModCandidate candidate, final @NotNull ASMDataTable table, final @NotNull JarFile jar, final @NotNull List<ModContainer> foundMods, final @NotNull MetadataCollection mc, final @NotNull CallbackInfo cbi) {
        ((Logger) FMLLog.log).setLevel(previousLevel);
    }

    @Inject(method = "findClassesASM", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V", shift = Shift.BEFORE, by = 1), cancellable = true)
    private void onFindClassesASMError(final @NotNull ModCandidate candidate, final @NotNull ASMDataTable table, final @NotNull JarFile jar, final @NotNull List<ModContainer> foundMods, final @NotNull MetadataCollection mc, final @NotNull CallbackInfo cbi) {
        cbi.cancel(); // Cancel outputting these errors, since they don't do us any good
        // We can't stop them either tho, because of Java 9+ compatibility with OW2 ASM.
    }
}
