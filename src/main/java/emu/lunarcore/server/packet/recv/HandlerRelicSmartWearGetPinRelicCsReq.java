package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.RelicSmartWearGetPinRelicCsReqOuterClass.RelicSmartWearGetPinRelicCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketRelicSmartWearGetPinRelicScRsp;

@Opcodes(CmdId.RelicSmartWearGetPinRelicCsReq)
public class HandlerRelicSmartWearGetPinRelicCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = RelicSmartWearGetPinRelicCsReq.parseFrom(data);
        session.send(new PacketRelicSmartWearGetPinRelicScRsp(req.getAvatarId()));
    }

}
