package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetSceneMapInfoCsReqOuterClass.GetSceneMapInfoCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetSceneMapInfoScRsp;

@Opcodes(CmdId.GetSceneMapInfoCsReq)
public class HandlerGetSceneMapInfoCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetSceneMapInfoCsReq.parseFrom(data);

        session.send(new PacketGetSceneMapInfoScRsp(req.getEntryIdList()));
    }

}
