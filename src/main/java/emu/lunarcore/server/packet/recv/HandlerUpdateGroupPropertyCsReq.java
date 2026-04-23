package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.UpdateGroupPropertyCsReqOuterClass.UpdateGroupPropertyCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketUpdateGroupPropertyScRsp;

@Opcodes(CmdId.UpdateGroupPropertyCsReq)
public class HandlerUpdateGroupPropertyCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = UpdateGroupPropertyCsReq.parseFrom(data);

        // TODO this is broken
        session.send(new PacketUpdateGroupPropertyScRsp(req.getFloorId(), req.getGroupId(), req.getPropertyName(), req.getPropertyValue()));
    }

}
