package emu.lunarcore.server.game;

import java.util.Set;

import org.reflections.Reflections;

import emu.lunarcore.LunarCore;
import emu.lunarcore.server.packet.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@SuppressWarnings("unchecked")
public class GameServerPacketHandler {
    private final Int2ObjectMap<PacketHandler> handlers;

    public GameServerPacketHandler() {
        this.handlers = new Int2ObjectOpenHashMap<>();

        this.registerHandlers();
    }

    public void registerPacketHandler(Class<? extends PacketHandler> handlerClass) {
        try {
            Opcodes opcode = handlerClass.getAnnotation(Opcodes.class);

            if (opcode == null || opcode.disabled() || opcode.value() <= 0) {
                return;
            }
            
            this.handlers.put(opcode.value(), handlerClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerHandlers() {
        Reflections reflections = new Reflections(LunarCore.class.getPackageName());
        Set<?> handlerClasses = reflections.getSubTypesOf(PacketHandler.class);

        for (Object obj : handlerClasses) {
            this.registerPacketHandler((Class<? extends PacketHandler>) obj);
        }

        LunarCore.getLogger().info("Game Server registered " + this.handlers.size() + " packet handlers");
    }

    public void handle(GameSession session, int cmdId, byte[] data) {
        PacketHandler handler = this.handlers.get(cmdId);

        if (handler != null) {
            // Check cooldown to prevent packet spam
            long timestamp = System.currentTimeMillis();
            if (session.getPacketCooldown().get(cmdId) >= timestamp && !CmdIdUtils.ALLOWED_FILTER_PACKETS.contains(cmdId)) {
                //LunarCore.getLogger().warn("Dropped a packet " + CmdIdUtils.getCmdIdName(cmdId));
                return;
            } else {
                session.getPacketCooldown().put(cmdId, timestamp + 5);
            }

            try {
                // Make sure session is ready for packets
                SessionState state = session.getState();

                if (cmdId == CmdId.PlayerHeartBeatCsReq) {
                    // Always continue if packet is ping request
                } else if (cmdId == CmdId.PlayerGetTokenCsReq) {
                    if (state != SessionState.WAITING_FOR_TOKEN) {
                        return;
                    }
                } else if (cmdId == CmdId.PlayerLoginCsReq) {
                    if (state != SessionState.WAITING_FOR_LOGIN) {
                        return;
                    }
                } else {
                    if (state != SessionState.ACTIVE) {
                        return;
                    }
                }
                
                // Handle packet
                handler.handle(session, data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Log unhandled packets
        //LunarCore.getLogger().info("Unhandled packet (" + cmdId + "): " + CmdIdUtils.getOpcodeName(cmdId));
    }
}
