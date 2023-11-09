package io.karma.bts.common;

import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Config(modid = BTSConstants.MODID,name = BTSConstants.MODID + "-common")
public final class CommonConfig {

    @Config.Comment("The time in ticks it takes the player to heal the configured amount.")
    @Config.RangeInt(min = 1,max = 1000)
    public static int healTime = 20;

    @Config.Comment("The amount of health restored each time the internal timer reaches the set amount of time.")
    @Config.RangeDouble(min = .01,max = 100)
    public static double healAmount = 2;

    @Config.Comment("The name of the NBT tag list in which pings are stored for every chunk.")
    public static String pingSaveTagName = "bts_pings";

    @Config.Comment("Button 0 Command")
    public static String button0Command = "/gamemode creative %player";
    @Config.Comment("Button 1 Command")
    public static String button1Command = "/gamemode survival %player";

    @Config.Comment("Button 2 Command")
    public static String button2Command = "/gamemode creative %player";
    @Config.Comment("Button 3 Command")
    public static String button3Command = "/gamemode survival %player";

    @Config.Comment("Button 4 Command")
    public static String button4Command = "/gamemode creative %player";
    @Config.Comment("Button 5 Command")
    public static String button5Command = "/gamemode survival %player";

    @Config.Comment("Button 6 Command")
    public static String button6Command = "/gamemode creative %player";
    @Config.Comment("Button 7 Command")
    public static String button7Command = "/gamemode survival %player";
    @Config.Comment("Button Spawn Command")
    public static String buttonSpawnCommand = "/tp %player 0 64 0";

    @Config.Comment("Armament Haki Keybind Command")
    public static String armamentKeybindCommand = "/effect %player strength 60 1";
    @Config.Comment("Observation Haki Keybind Command")
    public static String observationKeybindCommand = "/effect %player night_vision 60 1";
    @Config.Comment("Dash Keybind Command")
    public static String dashKeybindCommand = "/effect %player speed 1 3";
    @Config.Comment("Taunt Keybind Command")
    public static String tauntKeybindCommand = "/say %player Please just do better :)";
    @Config.Ignore
    public static final List<Supplier<String>> commands = new ArrayList<>();

    //    public static KeyBinding ARMAMENT_ACTIVATION;
    //    public static KeyBinding OBSERVATION_ACTIVATION;
    //    public static KeyBinding DASH;
    //    public static KeyBinding TAUNT;


    static {
        commands.add(() -> button0Command);
        commands.add(() -> button1Command);
        commands.add(() -> button2Command);
        commands.add(() -> button3Command);
        commands.add(() -> button4Command);
        commands.add(() -> button5Command);
        commands.add(() -> button6Command);
        commands.add(() -> button7Command);
        commands.add(() -> buttonSpawnCommand);

    }

    // @formatter:on
    private CommonConfig() {
    }
    // @formatter:off
}
