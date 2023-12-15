package io.karma.bts.common.network;

import io.karma.bts.common.util.KeybindInput;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class UseKeybindPacket implements IMessage {

    public UseKeybindPacket(){}
    public UseKeybindPacket(KeybindInput id) {
        this.keybindInput = id;
    }
    public KeybindInput keybindInput;
    @Override
    public void fromBytes(ByteBuf buf) {
        keybindInput = KeybindInput.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(keybindInput.ordinal());
    }
}
