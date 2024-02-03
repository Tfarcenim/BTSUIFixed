package io.karma.bts.common.mixins;

import io.karma.bts.common.BTSMod;
import io.karma.bts.common.CommonEventHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerMP.class)
public abstract class ServerPlayerMixin {

    @Shadow public NetHandlerPlayServer connection;

    /*@Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true, remap = MixinConfigPlugin.REMAP)
    private void interceptMessage(final @NotNull CallbackInfo cbi) {
        if (CommonEventHandler.muteMessage) {
            cbi.cancel();
        }
    }*/

    @Overwrite(remap = MixinConfigPlugin.REMAP)
    public void sendMessage(ITextComponent component) {
        if (!CommonEventHandler.muteMessage) {
            BTSMod.LOGGER.warn("blocked message");
            this.connection.sendPacket(new SPacketChat(component));
        }
    }
}
