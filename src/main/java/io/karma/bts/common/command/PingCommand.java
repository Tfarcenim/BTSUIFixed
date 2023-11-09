package io.karma.bts.common.command;

import io.karma.bts.common.PingHandler;
import io.karma.bts.common.util.LangUtils;
import io.karma.bts.common.util.PingColor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

public class PingCommand extends CommandTreeBase {
    public static final String NAME = "ping";

    public PingCommand() {
        addSubcommand(new CommandTreeHelp(this));
        addSubcommand(new Add());
        addSubcommand(new Remove());
        addSubcommand(new List());
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
        return "/io.karma.bts ping <subcommand> [...]";
    }

    private static final class Add extends CommandBase {
        public static final String NAME = "add";

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
            return "/io.karma.bts ping add <color> [<player name>|<x>, <y>, <z>]";
        }

        @Override
        public void execute(final @NotNull MinecraftServer server, final @NotNull ICommandSender sender, final @NotNull String @NotNull [] args) throws CommandException {
            final World world = sender.getEntityWorld();
            final int numArgs = args.length;

            if (numArgs == 0) {
                throw new CommandException(LangUtils.key("command", "inv_arg_count"));
            }

            // @formatter:off
            final Optional<PingColor> color = Arrays.stream(PingColor.values())
                                                    .filter(c -> c.name().equalsIgnoreCase(args[0]))
                                                    .findFirst();
            // @formatter:on

            EnumSet<PingColor> colors;

            if (color.isPresent()) {
                colors = EnumSet.of(color.get());
            }
            else if (args[0].equalsIgnoreCase("random")) {
                final PingColor[] allColors = PingColor.values();
                colors = EnumSet.of(allColors[world.rand.nextInt(allColors.length)]);
            }
            else {
                throw new CommandException(LangUtils.key("command", "inv_ping_color"));
            }

            BlockPos pos;

            switch (numArgs) {
                case 1:
                    final Entity entity = sender.getCommandSenderEntity();

                    if (entity == null) {
                        throw new CommandException(LangUtils.key("command", "unknown_error"));
                    }

                    pos = entity.getPosition();
                    break;
                case 2:
                    // @formatter:off
                    pos = server.getPlayerList()
                                .getPlayers()
                                .stream()
                                .filter(p -> p.getDisplayNameString().equals(args[1]))
                                .findFirst()
                                .orElseThrow(() -> new CommandException(LangUtils.key("command", "inv_player")))
                                .getPosition();
                    // @formatter:on
                    break;
                case 4:
                    try {
                        final int x = Integer.parseInt(args[1]);
                        final int y = Integer.parseInt(args[2]);
                        final int z = Integer.parseInt(args[3]);
                        pos = new BlockPos(x, y, z);
                    }
                    catch (Throwable t) {
                        throw new CommandException(LangUtils.key("command", "unknown_error"));
                    }

                    break;
                default:
                    throw new CommandException(LangUtils.key("command", "inv_arg_count"));
            }

            PingHandler.INSTANCE.addPing(world, pos, colors, true, null);
            world.getChunk(pos).markDirty();
        }
    }


    private static final class Remove extends CommandBase {
        public static final String NAME = "remove";

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
            return "/io.karma.bts ping remove <color> [<player name>|<x>, <y>, <z>]";
        }

        // TODO: translate these
        @Override
        public void execute(final @NotNull MinecraftServer server, final @NotNull ICommandSender sender, final @NotNull String @NotNull [] args) throws CommandException {
            final World world = sender.getEntityWorld();
            final int numArgs = args.length;

            if (numArgs == 0) {
                throw new CommandException(LangUtils.key("command", "inv_arg_count"));
            }

            // @formatter:off
            final Optional<PingColor> color = Arrays.stream(PingColor.values())
                .filter(c -> c.name().equalsIgnoreCase(args[0]))
                .findFirst();
            // @formatter:on

            EnumSet<PingColor> colors;

            if (color.isPresent()) {
                colors = EnumSet.of(color.get());
            }
            else if (args[0].equalsIgnoreCase("all")) {
                colors = EnumSet.allOf(PingColor.class);
            }
            else if (args[0].equalsIgnoreCase("random")) {
                final PingColor[] allColors = PingColor.values();
                colors = EnumSet.of(allColors[world.rand.nextInt(allColors.length)]);
            }
            else {
                throw new CommandException(LangUtils.key("command", "inv_ping_color"));
            }

            BlockPos pos;

            switch (numArgs) {
                case 1:
                    final Entity entity = sender.getCommandSenderEntity();

                    if (entity == null) {
                        throw new CommandException(LangUtils.key("command", "unknown_error"));
                    }

                    pos = entity.getPosition();
                    break;
                case 2:
                    // @formatter:off
                    pos = server.getPlayerList()
                        .getPlayers()
                        .stream()
                        .filter(p -> p.getDisplayNameString().equals(args[1]))
                        .findFirst()
                        .orElseThrow(() -> new CommandException(String.format("Invalid player name '%s'", args[1])))
                        .getPosition();
                    // @formatter:on
                    break;
                case 4:
                    try {
                        final int x = Integer.parseInt(args[1]);
                        final int y = Integer.parseInt(args[2]);
                        final int z = Integer.parseInt(args[3]);
                        pos = new BlockPos(x, y, z);
                    }
                    catch (Throwable t) {
                        throw new CommandException(LangUtils.key("command", "unknown_error"));
                    }

                    break;
                default:
                    throw new CommandException(LangUtils.key("command", "inv_arg_count"));
            }

            PingHandler.INSTANCE.removePing(world, pos, colors, true, null);
            world.getChunk(pos).markDirty();
        }
    }

    private static final class List extends CommandBase {
        @Override
        public int getRequiredPermissionLevel() {
            return 2;
        }

        @Override
        public @NotNull String getName() {
            return "list";
        }

        @Override
        public @NotNull String getUsage(final @NotNull ICommandSender sender) {
            return "/io.karma.bts ping list [<dimension>] [<page>]";
        }

        @Override
        public void execute(final @NotNull MinecraftServer server, final @NotNull ICommandSender sender, final @NotNull String @NotNull [] args) throws CommandException {

        }
    }
}
