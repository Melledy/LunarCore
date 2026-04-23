package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetBigDataRecommendCsReqOuterClass.GetBigDataRecommendCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetBigDataRecommendScRsp;

@Opcodes(CmdId.GetBigDataRecommendCsReq)
public class HandlerGetBigDataRecommendCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        GetBigDataRecommendCsReq req = GetBigDataRecommendCsReq.parseFrom(data);
        session.send(new PacketGetBigDataRecommendScRsp(req));
    }

}
