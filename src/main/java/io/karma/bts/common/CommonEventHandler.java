package io.karma.bts.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.Sys;

import java.util.List;

@Mod.EventBusSubscriber
public final class CommonEventHandler {

    public static boolean muteMessage;

    @SubscribeEvent
    public static void queryMana(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.player instanceof EntityPlayerMP) {

            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) event.player;
            MinecraftServer server = entityPlayerMP.server;
             muteMessage = true;
            //entityPlayerMP.sendMessage(new TextComponentString(getManaMessage(entityPlayerMP)));
              server.getCommandManager().executeCommand(entityPlayerMP, "ms mana show");
            muteMessage = false;
        }
    }

    public static ITextComponent getNewMesaage(ITextComponent original) {
        if (original instanceof TextComponentString && CommonEventHandler.muteMessage) {
            TextComponentString textComponentString = (TextComponentString) original;
            List<ITextComponent> siblings = textComponentString.getSiblings();
            for (ITextComponent component : siblings) {
                System.out.println(component);
            }
            return original;
        }
        return original;
    }

    private static String getManaMessage(EntityPlayerMP bar) {
        return "Mana: {=====}["+(int)bar.getFoodStats().getSaturationLevel()+"/"+20+"]";
    }
}
