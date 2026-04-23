package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetOfferingInfoCsReqOuterClass.GetOfferingInfoCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetOfferingInfoScRsp;

@Opcodes(CmdId.GetOfferingInfoCsReq)
public class HandlerGetOfferingInfoCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetOfferingInfoCsReq.parseFrom(data);
        session.send(new PacketGetOfferingInfoScRsp(req.getOfferingIdList()));
    }

}
