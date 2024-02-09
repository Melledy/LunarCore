package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.InteractChargerCsReqOuterClass.InteractChargerCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketInteractChargerScRsp;
import emu.lunarcore.server.packet.send.PacketUpdateEnergyScNotify;

@Opcodes(CmdId.InteractChargerCsReq)
public class HandlerInteractChargerCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = InteractChargerCsReq.parseFrom(data);
        session.send(new PacketInteractChargerScRsp(req.getChargerInfo()));
        session.send(new PacketUpdateEnergyScNotify());
    }

}
