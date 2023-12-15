package io.karma.bts.common.mixins;

import io.karma.bts.common.CommonEventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "sendMessage",at = @At("HEAD"),cancellable = true,remap = MixinConfigPlugin.REMAP)
    private void blockMessage(ITextComponent component, CallbackInfo ci) {
        if (CommonEventHandler.muteMessage) {
            ci.cancel();
        }
    }
}
