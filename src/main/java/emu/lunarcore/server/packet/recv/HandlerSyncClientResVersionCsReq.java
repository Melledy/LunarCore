package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SyncClientResVersionCsReqOuterClass.SyncClientResVersionCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSyncClientResVersionScRsp;

@Opcodes(CmdId.SyncClientResVersionCsReq)
public class HandlerSyncClientResVersionCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SyncClientResVersionCsReq.parseFrom(data);

        session.send(new PacketSyncClientResVersionScRsp(req.getClientResVersion()));
    }

}
