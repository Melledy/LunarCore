package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetSignatureCsReqOuterClass.SetSignatureCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetSignatureScRsp;

@Opcodes(CmdId.SetSignatureCsReq)
public class HandlerSetSignatureCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetSignatureCsReq.parseFrom(data);
        
        session.getPlayer().setSignature(req.getSignature());
        session.send(new PacketSetSignatureScRsp(session.getPlayer()));
    }

}
