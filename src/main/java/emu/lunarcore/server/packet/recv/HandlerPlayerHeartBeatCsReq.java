package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.PlayerHeartbeatCsReqOuterClass.PlayerHeartbeatCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketPlayerHeartBeatScRsp;

@Opcodes(CmdId.PlayerHeartBeatCsReq)
public class HandlerPlayerHeartBeatCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // Parse req data
        var req = PlayerHeartbeatCsReq.parseFrom(data);

        // Send heartbeat response back
        session.send(new PacketPlayerHeartBeatScRsp(req.getClientTimeMs()));
    }

}
