package io.karma.bts.common.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;
import org.jetbrains.annotations.NotNull;

public class BTSCommand extends CommandTreeBase {
    public static final String NAME = "bts";

    public BTSCommand() {
        addSubcommand(new CommandTreeHelp(this));
        addSubcommand(new PingCommand());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public @NotNull String getName() {
        return NAME;
    }

    @Override
    public @NotNull String getUsage(final @NotNull ICommandSender sender) {
        return "/io.karma.bts <subcommand> [...]";
    }
}
