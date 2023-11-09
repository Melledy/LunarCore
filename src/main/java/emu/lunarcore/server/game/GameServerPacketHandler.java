package emu.lunarcore.server.game;

import java.util.Set;

import org.reflections.Reflections;

import emu.lunarcore.LunarCore;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.SessionState;
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

            PacketHandler packetHandler = handlerClass.getDeclaredConstructor().newInstance();

            this.handlers.put(opcode.value(), packetHandler);
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

        // Debug
        LunarCore.getLogger().info("Game Server registered " + this.handlers.size() + " packet handlers");
    }

    public void handle(GameSession session, int opcode, byte[] header, byte[] data) {
        PacketHandler handler = this.handlers.get(opcode);

        if (handler != null) {
            try {
                // Make sure session is ready for packets
                SessionState state = session.getState();

                if (opcode == CmdId.PlayerHeartBeatCsReq) {
                    // Always continue if packet is ping request
                } else if (opcode == CmdId.PlayerGetTokenCsReq) {
                    if (state != SessionState.WAITING_FOR_TOKEN) {
                        return;
                    }
                } else if (opcode == CmdId.PlayerLoginCsReq) {
                    if (state != SessionState.WAITING_FOR_LOGIN) {
                        return;
                    }
                } else {
                    if (state != SessionState.ACTIVE) {
                        return;
                    }
                }

                // Handle packet
                handler.handle(session, header, data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            return; // Packet successfully handled
        }

        // Log unhandled packets
        //LunarCore.getLogger().info("Unhandled packet (" + opcode + "): " + CmdIdUtils.getOpcodeName(opcode));
    }
}
