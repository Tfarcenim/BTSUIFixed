package io.karma.bts.common.mixins;

import io.karma.bts.common.hooks.BTSPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements BTSPlayer {

    public EntityPlayerMixin(final @NotNull World world) {
        super(world);
    }

    @Inject(method = "entityInit", at = @At("TAIL"),remap = MixinConfigPlugin.REMAP)
    private void onEntityInit(final @NotNull CallbackInfo cbi) {
    }

}
