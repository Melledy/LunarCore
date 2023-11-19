package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetFirstTalkByPerformanceNpcCsReqOuterClass.GetFirstTalkByPerformanceNpcCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetFirstTalkByPerformanceNpcScRsp;

@Opcodes(CmdId.GetFirstTalkByPerformanceNpcCsReq)
public class HandlerGetFirstTalkByPerformanceNpcCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetFirstTalkByPerformanceNpcCsReq.parseFrom(data);
        
        session.send(new PacketGetFirstTalkByPerformanceNpcScRsp(req));
    }

}
