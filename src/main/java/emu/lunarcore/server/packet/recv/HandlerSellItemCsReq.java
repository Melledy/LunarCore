package emu.lunarcore.server.packet.recv;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.proto.ItemCostOuterClass.ItemCost;
import emu.lunarcore.proto.SellItemCsReqOuterClass.SellItemCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketSellItemScRsp;

@Opcodes(CmdId.SellItemCsReq)
public class HandlerSellItemCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = SellItemCsReq.parseFrom(data);

        List<ItemParam> items = new ArrayList<>(req.getItemCostList().getItemList().length());
        for (ItemCost cost : req.getItemCostList().getItemList()) {
            items.add(new ItemParam(cost));
        }

        var returnItems = session.getServer().getInventoryService().sellItems(session.getPlayer(), req.getToMaterial(), items);
        session.send(new PacketSellItemScRsp(returnItems));
    }

}
