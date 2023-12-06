package emu.lunarcore.server.packet.recv;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ComposeItemCsReqOuterClass.ComposeItemCsReq;
import emu.lunarcore.proto.ItemCostOuterClass.ItemCost;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketComposeItemScRsp;

@Opcodes(CmdId.ComposeItemCsReq)
public class HandlerComposeItemCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        var req = ComposeItemCsReq.parseFrom(data);
        
        List<ItemParam> costItems = new ArrayList<>(req.getComposeItemList().getItemList().length());
        for (ItemCost cost : req.getComposeItemList().getItemList()) {
            costItems.add(new ItemParam(cost));
        }
        
        List<GameItem> returnList = session.getServer().getInventoryService().composeItem(
                session.getPlayer(),
                req.getComposeId(),
                req.getCount(),
                costItems
        );
        
        session.send(new PacketComposeItemScRsp(req.getComposeId(), req.getCount(), returnList));
    }

}
