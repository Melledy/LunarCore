package emu.lunarcore.server.packet.recv;

import emu.lunarcore.proto.BuyGoodsCsReqOuterClass.BuyGoodsCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketBuyGoodsScRsp;

@Opcodes(CmdId.BuyGoodsCsReq)
public class HandlerBuyGoodsCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = BuyGoodsCsReq.parseFrom(data);
        
        var items = session.getServer().getShopService().buyGoods(session.getPlayer(), req.getShopId(), req.getGoodsId(), req.getGoodsNum());
        session.send(new PacketBuyGoodsScRsp(req, items));
    }

}
