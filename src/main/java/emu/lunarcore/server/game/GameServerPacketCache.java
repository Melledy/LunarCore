package emu.lunarcore.server.game;

import org.reflections.Reflections;

import emu.lunarcore.LunarCore;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CacheablePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class GameServerPacketCache {
    private final Int2ObjectMap<ByteBuf> packets;
    
    public GameServerPacketCache() {
        this.packets = new Int2ObjectOpenHashMap<>();
        
        // Pre cache packets
        var list = new Reflections(LunarCore.class.getPackageName()).getTypesAnnotatedWith(CacheablePacket.class);
        for (Class<?> packetClass : list) {
            try {
                if (BasePacket.class.isAssignableFrom(packetClass)) {
                    var packet = (BasePacket) packetClass.getDeclaredConstructor().newInstance();
                    this.packets.put(packet.getCmdId(), Unpooled.wrappedBuffer(packet.build()));
                }
            } catch (Exception e) {
                // Ignored
            }
        }
    }
    
    public ByteBuf getCachedPacket(int cmdId) {
        return this.packets.computeIfAbsent(cmdId, id -> Unpooled.wrappedBuffer(new BasePacket(id).build()));
    }
}
