package emu.lunarcore.server.packet.send;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetGachaInfoScRsp extends BasePacket {

    public PacketGetGachaInfoScRsp(GameSession session) {
        super(CmdId.GetGachaInfoScRsp);

        this.setData(session.getServer().getGachaService().toProto(session.getPlayer()));
    }
}
