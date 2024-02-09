package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.DeployRotaterCsReqOuterClass.DeployRotaterCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketDeployRotaterScRsp;

@Opcodes(CmdId.DeployRotaterCsReq)
public class HandlerDeployRotaterCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = DeployRotaterCsReq.parseFrom(data);

        session.send(new PacketDeployRotaterScRsp(req.getRotaterData()));
    }

}
