package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetPlayerDetailInfoCsReqOuterClass.GetPlayerDetailInfoCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetPlayerDetailInfoScRsp;

@Opcodes(CmdId.GetPlayerDetailInfoCsReq)
public class HandlerGetPlayerDetailInfoCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] header, byte[] data) throws Exception {
        var req = GetPlayerDetailInfoCsReq.parseFrom(data);

        session.send(new PacketGetPlayerDetailInfoScRsp());
    }

}
