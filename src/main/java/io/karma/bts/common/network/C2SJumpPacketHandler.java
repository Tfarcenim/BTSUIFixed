package io.karma.bts.common.network;

import io.karma.bts.common.CommonConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SJumpPacketHandler implements IMessageHandler<C2SJumpPacket, IMessage> {
    @Override
    public IMessage onMessage(C2SJumpPacket message, MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            handle(ctx.getServerHandler().player);
        });
        return null;
    }

    private void handle(EntityPlayerMP playerMP) {
        String command = CommonConfig.doubleJumpCommand;
        command = command.replace("%player",playerMP.getName());
        playerMP.server.getCommandManager().executeCommand(playerMP.server,command);
    }
}
