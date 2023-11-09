package io.karma.bts.common.mixins;

import io.karma.bts.common.CommonConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FoodStats.class)
public final class FoodStatsMixin {
    // Re-use this field, because why not
    @Shadow
    private int foodTimer;

    /**
     * @param player The current player entity being updated.
     * @reason We want to replace the basic healing mechanics
     *         of the player to work without hunger entirely.
     * @author KitsuneAlex
     */
    @Overwrite
    public void onUpdate(final @NotNull EntityPlayer player) {
        if (!player.isCreative() && player.getHealth() < player.getMaxHealth()) {
            if (foodTimer < CommonConfig.healTime) {
                foodTimer++;
            }
            else {
                player.heal((float) CommonConfig.healAmount);
                foodTimer = 0;
            }
        }
    }
}
