package io.karma.bts.common;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

@Name(BTSConstants.NAME + " Core")
@MCVersion(BTSConstants.MC_VERSION)
@TransformerExclusions({"org.spongepowered.", "noppes.npcs."})
public final class BTSLoadingPlugin implements IFMLLoadingPlugin {
    private static Field ucpField;

    static {
        try {
            ucpField = URLClassLoader.class.getDeclaredField("ucp");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        ucpField.setAccessible(true);

      //      final URL selfUrl = BTSLoadingPlugin.class.getProtectionDomain().getCodeSource().getLocation();
      //      prioritizeLoadURL(Launch.classLoader, selfUrl);
      //      prioritizeLoadURL((URLClassLoader) ClassLoader.getSystemClassLoader(), selfUrl);
    }

    private static void removeLoadURL(final @Nullable URLClassLoader classLoader, final @Nullable URL url) throws Exception {
        if (classLoader == null || url == null) {
            return;
        }

        //final ArrayList<URL> urls = new ArrayList<>(Arrays.asList(classLoader.getURLs()));
      //  urls.remove(url);
      //  ucpField.set(classLoader, new URLClassPath(urls.toArray(new URL[0])));
    }

    private static void addLoadURL(final @Nullable URLClassLoader classLoader, final @Nullable URL url, final int index) throws Exception {
        if (classLoader == null || url == null) {
            return;
        }

   //     final ArrayList<URL> urls = new ArrayList<>(Arrays.asList(classLoader.getURLs()));
   //     urls.add(index, url);
   //     ucpField.set(classLoader, new URLClassPath(urls.toArray(new URL[0])));
    }

    private static void prioritizeLoadURL(final @NotNull URLClassLoader classLoader, final @Nullable URL url) throws Exception {
        removeLoadURL(classLoader, url);
        addLoadURL(classLoader, url, 0);
    }

    @Override
    public @Nullable String[] getASMTransformerClass() {
        MixinBootstrap.init();
        Mixins.addConfiguration(String.format("mixins.%s.common.json", BTSConstants.MODID));
        return null;
    }

    @Override
    public @Nullable String getModContainerClass() {
        return null;
    }

    @Override
    public @Nullable String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(final @NotNull Map<String, Object> data) {

    }

    @Override
    public @Nullable String getAccessTransformerClass() {
        return null;
    }
}
