package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SelectPamSkinCsReqOuterClass.SelectPamSkinCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSelectPamSkinScRsp;

@Opcodes(CmdId.SelectPamSkinCsReq)
public class HandlerSelectPamSkinCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SelectPamSkinCsReq.parseFrom(data);

        session.send(new PacketSelectPamSkinScRsp(req.getPamSkinId(), session.getPlayer()));
    }

}
