package io.karma.bts.common;

import io.karma.bts.common.util.PingColor;
import io.karma.bts.server.network.PacketAddPing;
import io.karma.bts.server.network.PacketClearPings;
import io.karma.bts.server.network.PacketRemovePing;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class PingHandler {
    public static final PingHandler INSTANCE = new PingHandler();

    private final Int2ObjectOpenHashMap<HashMap<BlockPos,EnumSet<PingColor>>> pings = new Int2ObjectOpenHashMap<>();

    // @formatter:off
    private PingHandler() {}
    // @formatter:on

    private void sendClearPingsPacket(final @NotNull World world, final @Nullable UUID uuid) {
        BTSMod.LOGGER.debug("Sending PacketClearPings");

        final PacketClearPings packet = new PacketClearPings();

        if (uuid != null) {
            final EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByUUID(uuid);

            if (player == null) {
                BTSMod.LOGGER.warn("Could not send packet to player {}", uuid);
                return;
            }

            BTSMod.CHANNEL.sendTo(packet, player);
        }
        else {
            BTSMod.CHANNEL.sendToAll(packet);
        }
    }

    private void sendAddPingPacket(final @NotNull World world, final @NotNull BlockPos pos, final @NotNull Collection<PingColor> colors, final @Nullable UUID uuid) {
        BTSMod.LOGGER.debug("Sending PacketAddPing for ping at [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());

        final PacketAddPing packet = new PacketAddPing();
        packet.pos = pos;
        packet.colors.addAll(colors);

        if (uuid != null) {
            final EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByUUID(uuid);

            if (player == null) {
                BTSMod.LOGGER.warn("Could not send packet to player {}", uuid);
                return;
            }

            BTSMod.CHANNEL.sendTo(packet, player);
        }
        else {
            BTSMod.CHANNEL.sendToAll(packet);
        }
    }

    private void sendRemovePingPacket(final @NotNull World world, final @NotNull BlockPos pos, final @NotNull Collection<PingColor> colors, final @Nullable UUID uuid) {
        BTSMod.LOGGER.debug("Sending PacketRemovePing for ping at [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());

        final PacketRemovePing packet = new PacketRemovePing();
        packet.pos = pos;
        packet.colors.addAll(colors);

        if (uuid != null) {
            final EntityPlayerMP player = (EntityPlayerMP) world.getPlayerEntityByUUID(uuid);

            if (player == null) {
                BTSMod.LOGGER.warn("Could not send packet to player {}", uuid);
                return;
            }

            BTSMod.CHANNEL.sendTo(packet, player);
        }
        else {
            BTSMod.CHANNEL.sendToAll(packet);
        }
    }

    public void addPing(final @Nullable World world, final @Nullable BlockPos pos, final boolean sendPacket, final @Nullable UUID uuid, final @Nullable PingColor color, final PingColor... colors) {
        if (world == null || pos == null || color == null) {
            return;
        }

        addPing(world, pos, EnumSet.of(color, colors), sendPacket, uuid);
    }

    public void addPing(final @Nullable World world, final @Nullable BlockPos pos, final @Nullable Collection<PingColor> colors, final boolean sendPacket, final @Nullable UUID uuid) {
        if (world == null || world.isRemote || pos == null || colors == null || colors.isEmpty()) {
            return;
        }

        final int dimensionId = world.provider.getDimension();
        HashMap<BlockPos, EnumSet<PingColor>> pings = this.pings.get(dimensionId);

        if (pings == null) {
            pings = new HashMap<>();
            this.pings.put(dimensionId, pings);
        }

        EnumSet<PingColor> colorSet = pings.get(pos);

        if (colorSet == null) {
            colorSet = EnumSet.copyOf(colors);
            pings.put(pos, colorSet);
        }
        else {
            colorSet.addAll(colors);
        }

        if (sendPacket) {
            sendAddPingPacket(world, pos, colors, uuid);
        }
    }

    public void removePing(final @Nullable World world, final @Nullable BlockPos pos, final boolean sendPacket, final @Nullable UUID uuid, @Nullable PingColor color, final PingColor... colors) {
        if (pos == null || color == null) {
            return;
        }

        removePing(world, pos, EnumSet.of(color, colors), sendPacket, uuid);
    }

    public void removePing(final @Nullable World world, final @Nullable BlockPos pos, final @Nullable Collection<PingColor> colors, final boolean sendPacket, final @Nullable UUID uuid) {
        if (world == null || world.isRemote || pos == null || colors == null || colors.isEmpty()) {
            return;
        }

        final int dimensionId = world.provider.getDimension();
        final HashMap<BlockPos, EnumSet<PingColor>> pings = this.pings.get(dimensionId);

        if (pings == null) {
            return;
        }

        EnumSet<PingColor> colorSet = pings.get(pos);

        if (colorSet == null) {
            return;
        }

        colorSet.removeAll(colors);

        if (sendPacket) {
            sendRemovePingPacket(world, pos, colors, uuid);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        final Entity entity = event.getEntity();
        final World world = event.getWorld();

        if (!(entity instanceof EntityPlayer) || world.isRemote) {
            return;
        }

        final int dimensionId = world.provider.getDimension();
        final HashMap<BlockPos, EnumSet<PingColor>> pings = this.pings.get(dimensionId);

        if (pings == null) {
            return;
        }

        final UUID uuid = entity.getPersistentID();
        sendClearPingsPacket(world, uuid);

        final Set<Map.Entry<BlockPos, EnumSet<PingColor>>> entries = pings.entrySet();
        MutableBlockPos pos = new MutableBlockPos(0, 0, 0);

        for (final Map.Entry<BlockPos, EnumSet<PingColor>> ping : entries) {
            pos.setPos(ping.getKey());
            sendAddPingPacket(world, pos, ping.getValue(), uuid);
        }
    }

    @SubscribeEvent
    public void onChunkDataLoad(final @NotNull ChunkDataEvent.Load event) {
        final World world = event.getWorld();

        if (world.isRemote) {
            return;
        }

        final int dimensionId = world.provider.getDimension();
        HashMap<BlockPos, EnumSet<PingColor>> pings = this.pings.get(dimensionId);

        if (pings == null) {
            pings = new HashMap<>();
            this.pings.put(dimensionId, pings);
        }

        final NBTTagCompound tag = event.getData();
        final String key = CommonConfig.pingSaveTagName;

        if (tag.hasKey(key)) {
            final NBTTagList pingTags = tag.getTagList(key, NBT.TAG_COMPOUND);
            final int numPings = pingTags.tagCount();

            MutableBlockPos pos = new MutableBlockPos(0, 0, 0);

            for (int i = 0; i < numPings; i++) {
                final NBTTagCompound pingTag = pingTags.getCompoundTagAt(i);

                final BlockPos serializedPos = new BlockPos(pingTag.getInteger("posX"),pingTag.getInteger("posY"),pingTag.getInteger("posZ"));

                pos.setPos(serializedPos);

                // @formatter:off
                final List<PingColor> colors = Arrays.stream(pingTag.getIntArray("colors"))
                    .mapToObj(c -> PingColor.values()[c])
                    .collect(Collectors.toList());
                // @formatter:on

                pings.put(serializedPos, colors.isEmpty() ? EnumSet.noneOf(PingColor.class) : EnumSet.copyOf(colors));
                sendAddPingPacket(world, pos, colors, null);
            }
        }
    }

    @SubscribeEvent
    public void onChunkDataSave(final @NotNull ChunkDataEvent.Save event) {
        final Chunk chunk = event.getChunk();
        final World world = event.getWorld();

        if (world.isRemote) {
            return;
        }

        final int dimensionId = world.provider.getDimension();
        HashMap<BlockPos, EnumSet<PingColor>> pings = this.pings.get(dimensionId);

        if (pings == null) {
            return;
        }

        final NBTTagCompound tag = event.getData();
        final NBTTagList pingTags = new NBTTagList();

        final Set<Map.Entry<BlockPos, EnumSet<PingColor>>> entries = pings.entrySet();

        MutableBlockPos pos = new MutableBlockPos(0, 0, 0);

        for (final Map.Entry<BlockPos, EnumSet<PingColor>> ping : entries) {
            final BlockPos serializedPos = ping.getKey();
            pos.setPos(serializedPos);

            final Chunk currentChunk = world.getChunk(pos);

            if (chunk.x != currentChunk.x || chunk.z != currentChunk.z) {
                continue;
            }

            final NBTTagCompound pingTag = new NBTTagCompound();
            pingTag.setInteger("posX", serializedPos.getX());
            pingTag.setInteger("posY", serializedPos.getY());
            pingTag.setInteger("posZ", serializedPos.getZ());

            // @formatter:off
            pingTag.setIntArray("colors", ping.getValue()
                .stream()
                .mapToInt(PingColor::ordinal)
                .toArray());
            // @formatter:on

            pingTags.appendTag(pingTag);
        }

        tag.setTag(CommonConfig.pingSaveTagName, pingTags);
    }
}
