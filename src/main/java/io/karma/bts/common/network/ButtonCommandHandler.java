package io.karma.bts.common.network;

import io.karma.bts.common.BTSMod;
import io.karma.bts.common.CommonConfig;
import io.karma.bts.common.network.RunCommandPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ButtonCommandHandler implements IMessageHandler<RunCommandPacket, IMessage> {
    @Override
    public IMessage onMessage(RunCommandPacket message, MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
           handle(ctx.getServerHandler().player,message.button_id);
        });
        return null;
    }

    private void handle(EntityPlayerMP playerMP,int button_id) {
        if (button_id >=0 && button_id < CommonConfig.commands.size()) {
            String command = CommonConfig.commands.get(button_id).get();
            command = command.replace("%player",playerMP.getName());
            playerMP.server.getCommandManager().executeCommand(playerMP.server,command);
        }else {
            BTSMod.LOGGER.warn("invalid button id: " + button_id);
        }
    }
}
