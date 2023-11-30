package emu.lunarcore.server.packet.recv;

import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.proto.GetMainMissionCustomValueCsReqOuterClass.GetMainMissionCustomValueCsReq;
import emu.lunarcore.server.packet.send.PacketGetMainMissionCustomValueScRsp;

@Opcodes(CmdId.GetMainMissionCustomValueCsReq)
public class HandlerGetMainMissionCustomValueCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetMainMissionCustomValueCsReq.parseFrom(data);
        session.send(new PacketGetMainMissionCustomValueScRsp(req.getMainMissionIdList().toArray()));
    }
}
