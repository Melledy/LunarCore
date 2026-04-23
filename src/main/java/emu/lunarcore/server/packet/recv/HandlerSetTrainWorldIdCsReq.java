package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetTrainWorldIdCsReqOuterClass.SetTrainWorldIdCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetTrainWorldIdScRsp;

@Opcodes(CmdId.SetTrainWorldIdCsReq)
public class HandlerSetTrainWorldIdCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetTrainWorldIdCsReq.parseFrom(data);
        
        session.send(new PacketSetTrainWorldIdScRsp(req.getTrainWorldId()));
    }

}
