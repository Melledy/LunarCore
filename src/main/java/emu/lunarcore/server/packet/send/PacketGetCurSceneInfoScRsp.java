package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.GetCurSceneInfoScRspOuterClass.GetCurSceneInfoScRsp;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetCurSceneInfoScRsp extends BasePacket {

    public PacketGetCurSceneInfoScRsp(GameSession session) {
        super(CmdId.GetCurSceneInfoScRsp);

        var data = GetCurSceneInfoScRsp.newInstance()
                .setScene(session.getPlayer().getScene().toProto());

        this.setData(data);
    }
}
