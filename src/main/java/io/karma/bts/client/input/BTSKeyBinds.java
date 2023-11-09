package io.karma.bts.client.input;

import io.karma.bts.client.screen.HUDConfigScreen;
import io.karma.bts.client.screen.QuickMenuScreen;
import io.karma.bts.common.BTSMod;
import io.karma.bts.common.util.KeybindInput;
import io.karma.bts.server.network.UseKeybindPacket;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Keyboard;

/**
 * @author Alexander Hinze
 * @since 14/08/2022
 */
public final class BTSKeyBinds {
    public static KeyBinding HUD_CONFIG;
    public static KeyBinding QUICK_MENU;

    public static KeyBinding ARMAMENT_ACTIVATION;
    public static KeyBinding OBSERVATION_ACTIVATION;
    public static KeyBinding DASH;
    public static KeyBinding TAUNT;
    //- ARMAMENT HAKI ACTIVATION (J KEYBIND)
    //- OBSERVATION HAKI ACTIVATION (G KEYBIND)
    //- DASH (Q KEYBIND)
    //- MAIN MENU [The one that you will create] (M KEYBIND)
    //- TAUNT (don’t worry you don’t need to add an animation system)



    public static void register() {
        final FMLClientHandler handler = FMLClientHandler.instance();

        HUD_CONFIG = new CallbackKeyBinding("hud_config", Keyboard.KEY_ESCAPE, KeyModifier.CONTROL, () -> {
            handler.showGuiScreen(new HUDConfigScreen());
        });

        QUICK_MENU = new CallbackKeyBinding("quick_menu", Keyboard.KEY_Q, KeyModifier.ALT, () -> {
            handler.showGuiScreen(new QuickMenuScreen());
        });
        ARMAMENT_ACTIVATION = new CallbackKeyBinding("armament_haki_activation",Keyboard.KEY_J,() -> {
            BTSMod.CHANNEL.sendToServer(new UseKeybindPacket(KeybindInput.ARMAMENT));
        });
        OBSERVATION_ACTIVATION = new CallbackKeyBinding("observation_haki_activation",Keyboard.KEY_G,() -> {
            BTSMod.CHANNEL.sendToServer(new UseKeybindPacket(KeybindInput.OBSERVATION));
        });

        DASH = new CallbackKeyBinding("dash",Keyboard.KEY_Q,() -> {
            BTSMod.CHANNEL.sendToServer(new UseKeybindPacket(KeybindInput.DASH));
        });

        TAUNT = new CallbackKeyBinding("taunt",Keyboard.KEY_T,() -> {
            BTSMod.CHANNEL.sendToServer(new UseKeybindPacket(KeybindInput.TAUNT));
        });
    }
}
