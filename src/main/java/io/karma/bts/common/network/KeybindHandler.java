package io.karma.bts.common.network;

import io.karma.bts.common.util.KeybindInput;
import io.karma.bts.common.network.UseKeybindPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KeybindHandler implements IMessageHandler<UseKeybindPacket, IMessage> {
    @Override
    public IMessage onMessage(UseKeybindPacket message, MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
           handle(ctx.getServerHandler().player,message.keybindInput);
        });
        return null;
    }

    private void handle(EntityPlayerMP playerMP, KeybindInput button_id) {
        button_id.action.accept(playerMP);
    }
}
