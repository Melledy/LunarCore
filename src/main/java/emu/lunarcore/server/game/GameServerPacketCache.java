package emu.lunarcore.server.game;

import emu.lunarcore.server.packet.BasePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class GameServerPacketCache {
    private final Int2ObjectMap<ByteBuf> packets;
    
    public GameServerPacketCache() {
        this.packets = new Int2ObjectOpenHashMap<>();
    }
    
    public ByteBuf getCachedPacket(int cmdId) {
        return this.packets.computeIfAbsent(cmdId, id -> Unpooled.wrappedBuffer(new BasePacket(id).build()));
    }
}
