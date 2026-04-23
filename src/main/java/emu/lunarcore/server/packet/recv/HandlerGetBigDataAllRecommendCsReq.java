package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetBigDataAllRecommendCsReqOuterClass.GetBigDataAllRecommendCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetBigDataAllRecommendScRsp;

@Opcodes(CmdId.GetBigDataAllRecommendCsReq)
public class HandlerGetBigDataAllRecommendCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetBigDataAllRecommendCsReq.parseFrom(data);
        session.send(new PacketGetBigDataAllRecommendScRsp(req.getRecommendType()));
    }

}
