package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.SetHeroBasicTypeCsReqOuterClass.SetHeroBasicTypeCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSetHeroBasicTypeScRsp;

@Opcodes(CmdId.SetHeroBasicTypeCsReq)
public class HandlerSetHeroBasicTypeCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SetHeroBasicTypeCsReq.parseFrom(data);
        
        session.getPlayer().setHeroBasicType(req.getBasicTypeValue());
        session.send(new PacketSetHeroBasicTypeScRsp(session.getPlayer()));
    }

}
