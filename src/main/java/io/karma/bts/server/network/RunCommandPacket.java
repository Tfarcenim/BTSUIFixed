package io.karma.bts.server.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RunCommandPacket implements IMessage {

    public RunCommandPacket(){}
    public RunCommandPacket(int id) {
        this.button_id = id;
    }
    public int button_id;
    @Override
    public void fromBytes(ByteBuf buf) {
        button_id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(button_id);
    }
}
