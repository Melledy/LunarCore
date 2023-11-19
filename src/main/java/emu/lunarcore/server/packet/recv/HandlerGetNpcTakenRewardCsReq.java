package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetNpcTakenRewardCsReqOuterClass.GetNpcTakenRewardCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetNpcTakenRewardScRsp;

@Opcodes(CmdId.GetNpcTakenRewardCsReq)
public class HandlerGetNpcTakenRewardCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetNpcTakenRewardCsReq.parseFrom(data);
        
        session.send(new PacketGetNpcTakenRewardScRsp(req.getNpcId()));
    }

}
