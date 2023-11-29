package io.karma.bts.common.mixins;

import io.karma.bts.common.IJarDiscoverer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.JarDiscoverer;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.jar.JarFile;

@Mixin(JarDiscoverer.class)
public abstract class JarDiscovererMixin implements IJarDiscoverer {
    @Unique
    private HashSet<String> listedFiles;

    @Inject(method = "findClassesASM", at = @At("HEAD"), remap = false, cancellable = true)
    public void findClassesASMHead(ModCandidate candidate, ASMDataTable table, JarFile jar, List<ModContainer> foundMods, MetadataCollection mc, CallbackInfo ci) {
        if (this.listedFiles == null) {
            this.listedFiles = new HashSet<>();
        }
        String modPath = candidate.getModContainer().getAbsolutePath();
        if (!this.listedFiles.add(modPath)) {
            ci.cancel();
            FMLLog.log.info("Skipping findClassesASM on duplicate jar : {}", modPath);
        }
    }

    @Override
    public void clearASMListedFiles() {
        this.listedFiles = null;
    }
}
