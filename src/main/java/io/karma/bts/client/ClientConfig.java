package io.karma.bts.client;

import io.karma.bts.common.BTSConstants;
import net.minecraftforge.common.config.Config;

@Config(modid = BTSConstants.MODID,name = BTSConstants.MODID +"-client")
public final class ClientConfig {

    @Config.Comment("Enable/disable the entire in-game HUD.")
    public static boolean enableHud = true;
    @Config.Comment("The scale of the in-game HUD.")
    public static double hudScale = .75;
    @Config.Comment("The X-position of the in-game HUD.")
    public static int hudX = 10;
    @Config.Comment("The Y-position of the in-game HUD.")
    public static int hudY = 10;
    @Config.Comment("The button ID of the HUD config screen button in the in-game menu.")
    public static int hudButtonId = 1337;

    @Config.Comment("Enable/disable shader effects on the HP bar.")
    public static boolean enableHPShader = true;
    @Config.Comment("Enable/disable shader effects on the SP bar.")
    public static boolean enableSPShader = true;
    @Config.Comment("Enable/disable shader effects on the XP bar.")
    public static boolean enableXPShader = true;

    @Config.Comment("The maximum distance at which pings are visible in blocks.")
    @Config.RangeDouble(min = 4,max = 1024)
    public static double pingRenderDistance = 128;
    @Config.Comment("The opacity of the pings rendered in-world.")
    @Config.RangeDouble(max = 1)
    public static double pingOpacity = 0.7;
    @Config.Comment("The base scale of all pings.")
    @Config.RangeDouble(max = 2)

    public static double pingScale = 1;

    @Config.Comment("Hide keybinds from other mods")
    public static boolean hide_other_keybinds=true;

    // @formatter:off
    private ClientConfig() {}
    // @formatter:on

}
