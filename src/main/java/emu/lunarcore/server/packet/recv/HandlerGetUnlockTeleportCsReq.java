package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetUnlockTeleportCsReqOuterClass.GetUnlockTeleportCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetUnlockTeleportScRsp;

@Opcodes(CmdId.GetUnlockTeleportCsReq)
public class HandlerGetUnlockTeleportCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetUnlockTeleportCsReq.parseFrom(data);
        
        session.send(new PacketGetUnlockTeleportScRsp(req.getEntryIdList()));
    }

}
