package emu.lunarcore.server.packet;

public enum SessionState {
    INACTIVE,
    WAITING_FOR_TOKEN,
    WAITING_FOR_LOGIN,
    PICKING_CHARACTER,
    ACTIVE
}
