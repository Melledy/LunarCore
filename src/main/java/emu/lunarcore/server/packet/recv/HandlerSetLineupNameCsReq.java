package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetLineupNameCsReqOuterClass.SetLineupNameCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetLineupNameScRsp;

@Opcodes(CmdId.SetLineupNameCsReq)
public class HandlerSetLineupNameCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetLineupNameCsReq.parseFrom(data);

        var success = session.getPlayer().getLineupManager().changeLineupName(req.getIndex(), req.getName());
        session.send(new PacketSetLineupNameScRsp(req.getIndex(), success ? req.getName() : null));
    }

}
