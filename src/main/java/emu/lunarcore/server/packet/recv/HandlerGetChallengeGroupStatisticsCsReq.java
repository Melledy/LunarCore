package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetChallengeGroupStatisticsCsReqOuterClass.GetChallengeGroupStatisticsCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetChallengeGroupStatisticsScRsp;

@Opcodes(CmdId.GetChallengeGroupStatisticsCsReq)
public class HandlerGetChallengeGroupStatisticsCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetChallengeGroupStatisticsCsReq.parseFrom(data);
        session.send(new PacketGetChallengeGroupStatisticsScRsp(req.getGroupId()));
    }

}
