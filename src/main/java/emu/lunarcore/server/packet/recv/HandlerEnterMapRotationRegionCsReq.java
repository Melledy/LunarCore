package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.EnterMapRotationRegionCsReqOuterClass.EnterMapRotationRegionCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketEnterMapRotationRegionScRsp;

@Opcodes(CmdId.EnterMapRotationRegionCsReq)
public class HandlerEnterMapRotationRegionCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = EnterMapRotationRegionCsReq.parseFrom(data);
        session.send(new PacketEnterMapRotationRegionScRsp(req.getMotion()));
    }

}
