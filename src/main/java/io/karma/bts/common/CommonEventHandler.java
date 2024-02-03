package io.karma.bts.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public final class CommonEventHandler {

    public static boolean muteMessage;

    public static boolean manaSync = true;

    public static long update_rate = 5;

    @SubscribeEvent
    public static void queryMana(TickEvent.PlayerTickEvent event) {
        if (manaSync && event.phase == TickEvent.Phase.START && event.player instanceof EntityPlayerMP && event.player.world.getTotalWorldTime() % update_rate == 0) {

            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) event.player;
            MinecraftServer server = entityPlayerMP.server;
            muteMessage = true;
            server.getCommandManager().executeCommand(entityPlayerMP, "ms mana show");
            muteMessage = false;
        }
    }
}
