package io.karma.bts.common.mixins;

import io.karma.bts.common.BTSLoadingPlugin;
import io.karma.bts.common.hooks.BTSPlayer;
import io.karma.bts.common.util.PingColor;
import io.karma.bts.common.util.PingColorSerializer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements BTSPlayer {
    // This is something MC Development IDEA has to fix..
    @SuppressWarnings("all")
    private static final DataParameter<EnumSet<PingColor>> ASSIGNED_COLORS = EntityDataManager.createKey(EntityPlayer.class, PingColorSerializer.INSTANCE);

    public EntityPlayerMixin(final @NotNull World world) {
        super(world);
    }

    @Inject(method = "entityInit", at = @At("TAIL"),remap = MixinConfigPlugin.REMAP)
    private void onEntityInit(final @NotNull CallbackInfo cbi) {
        dataManager.register(ASSIGNED_COLORS, EnumSet.noneOf(PingColor.class));
    }

    @Override
    public @NotNull EnumSet<PingColor> getAssignedColors() {
        return dataManager.get(ASSIGNED_COLORS);
    }

    @Override
    public void setAssignedColors(final @NotNull EnumSet<PingColor> colors) {
        dataManager.set(ASSIGNED_COLORS, colors);
    }
}
