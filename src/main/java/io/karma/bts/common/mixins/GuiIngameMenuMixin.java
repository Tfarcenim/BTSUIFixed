package io.karma.bts.common.mixins;

import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiIngameMenu.class)
public class GuiIngameMenuMixin {

    @ModifyArg(method = "actionPerformed",at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V",ordinal = 2),remap = MixinConfigPlugin.REMAP)
    private GuiScreen redirectToMainMenu(GuiScreen multiplayer) {
        return new GuiMainMenu();
    }

}
