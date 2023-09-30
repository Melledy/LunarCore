package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetCurLineupDataScRspOuterClass.GetCurLineupDataScRsp;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetCurLineupDataScRsp extends BasePacket {

    public PacketGetCurLineupDataScRsp(GameSession session) {
        super(CmdId.GetCurLineupDataScRsp);

        var data = GetCurLineupDataScRsp.newInstance()
                .setLineup(session.getPlayer().getCurrentLineup().toProto());

        this.setData(data);
    }
}
