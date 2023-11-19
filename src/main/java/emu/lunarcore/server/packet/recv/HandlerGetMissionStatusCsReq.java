package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetMissionStatusCsReqOuterClass.GetMissionStatusCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetMissionStatusScRsp;

@Opcodes(CmdId.GetMissionStatusCsReq)
public class HandlerGetMissionStatusCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetMissionStatusCsReq.parseFrom(data);

        session.send(new PacketGetMissionStatusScRsp(req));
    }

}
