package io.karma.bts.common.util;

import io.karma.bts.common.CommonConfig;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.function.Consumer;
import java.util.function.Supplier;

public enum KeybindInput {
    ARMAMENT(playerMP -> runConfigCommand(playerMP,() -> CommonConfig.armamentKeybindCommand)),
    OBSERVATION(playerMP -> runConfigCommand(playerMP,() -> CommonConfig.observationKeybindCommand)),
    DASH(playerMP -> runConfigCommand(playerMP,() -> CommonConfig.dashKeybindCommand)),
    TAUNT(playerMP -> runConfigCommand(playerMP,() -> CommonConfig.tauntKeybindCommand));

    public final Consumer<EntityPlayerMP> action;

    KeybindInput(Consumer<EntityPlayerMP> action) {

        this.action = action;
    }

    static void runConfigCommand(EntityPlayerMP playerMP, Supplier<String> command) {
        String rawCommand = command.get();
        rawCommand = rawCommand.replace("%player",playerMP.getName());
        playerMP.server.getCommandManager().executeCommand(playerMP.server,rawCommand);
    }
}
