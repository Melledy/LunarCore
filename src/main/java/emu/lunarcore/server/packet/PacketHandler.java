package emu.lunarcore.server.packet;

import emu.lunarcore.server.game.GameSession;

public abstract class PacketHandler {
    public abstract void handle(GameSession session, byte[] data) throws Exception;
}
