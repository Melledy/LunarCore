package emu.lunarcore.server.packet;

import emu.lunarcore.server.game.GameSession;

public abstract class PacketHandler {
    protected static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public abstract void handle(GameSession session, byte[] data) throws Exception;
}
