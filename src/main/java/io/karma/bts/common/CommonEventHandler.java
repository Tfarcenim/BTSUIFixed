package io.karma.bts.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public final class CommonEventHandler {

    public static boolean muteMessage;

    @SubscribeEvent
    public static void queryMana(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.player instanceof EntityPlayerMP) {

            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) event.player;
            MinecraftServer server = entityPlayerMP.server;
           // entityPlayerMP.sendMessage(new TextComponentString(getManaMessage(entityPlayerMP)));
             muteMessage = true;
            server.getCommandManager().executeCommand(entityPlayerMP, "/ms mana show");
            muteMessage = false;
        }
    }

    private static String getManaMessage(EntityPlayerMP bar) {
        return "Mana: {=====}["+(int)bar.getFoodStats().getSaturationLevel()+"/"+20+"]";
    }
}
