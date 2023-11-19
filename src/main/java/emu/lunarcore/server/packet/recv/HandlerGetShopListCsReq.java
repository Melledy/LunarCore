package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.GetShopListCsReqOuterClass.GetShopListCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketGetShopListScRsp;

@Opcodes(CmdId.GetShopListCsReq)
public class HandlerGetShopListCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = GetShopListCsReq.parseFrom(data);
        
        session.send(new PacketGetShopListScRsp(req.getShopType()));
    }

}
