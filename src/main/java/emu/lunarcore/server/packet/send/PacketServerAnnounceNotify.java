package emu.lunarcore.server.packet.send;

import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketServerAnnounceNotify extends BasePacket {
    
    public PacketServerAnnounceNotify() {
        super(CmdId.ServerAnnounceNotify);

        byte[] byteArray = {
				(byte)0xE5, (byte)0x85, (byte)0x8D, (byte)0xE8, (byte)0xB4, (byte)0xB9, (byte)0xE6, (byte)0x9C, (byte)0x8D, (byte)0xE5,
				(byte)0x8A, (byte)0xA1, (byte)0xE5, (byte)0x99, (byte)0xA8, (byte)0xEF, (byte)0xBC, (byte)0x8C, (byte)0xE4, (byte)0xB9,
				(byte)0xB0, (byte)0xE6, (byte)0x9D, (byte)0xA5, (byte)0xE7, (byte)0x9A, (byte)0x84, (byte)0xE6, (byte)0x98, (byte)0xAF,
				(byte)0xE5, (byte)0x82, (byte)0xBB, (byte)0xE9, (byte)0x80, (byte)0xBC
        };

        this.setData(byteArray);
    }
}