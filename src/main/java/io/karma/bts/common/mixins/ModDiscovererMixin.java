package io.karma.bts.common.mixins;

import io.karma.bts.common.IJarDiscoverer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ContainerType;
import net.minecraftforge.fml.common.discovery.ModDiscoverer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(ModDiscoverer.class)
public abstract class ModDiscovererMixin {
    @Inject(method = "identifyMods", at = @At(value = "RETURN"), remap = false)
    public void identifyModsReturn(CallbackInfoReturnable<List<ModContainer>> cir) {
        FMLLog.log.info("Clearing JarDiscoverer listedFiles to save memory");
        try {
            Field discovererField = ContainerType.class.getDeclaredField("discoverer");
            discovererField.setAccessible(true);
            ((IJarDiscoverer) discovererField.get(ContainerType.JAR)).clearASMListedFiles();
        } catch (NoSuchFieldException | IllegalAccessException e) { // This should not happen, it is also not a serious issue if it does for some reason
            e.printStackTrace(); // Still print the exception for debugging purposes
        }
    }
}
