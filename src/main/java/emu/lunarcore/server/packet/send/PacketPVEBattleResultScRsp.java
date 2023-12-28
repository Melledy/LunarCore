package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.proto.ItemListOuterClass.ItemList;
import emu.lunarcore.proto.PVEBattleResultCsReqOuterClass.PVEBattleResultCsReq;
import emu.lunarcore.proto.PVEBattleResultScRspOuterClass.PVEBattleResultScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketPVEBattleResultScRsp extends BasePacket {
    
    public PacketPVEBattleResultScRsp() {
        super(CmdId.PVEBattleResultScRsp);

        var data = PVEBattleResultScRsp.newInstance()
                .setRetcode(1);

        this.setData(data);
    }

    public PacketPVEBattleResultScRsp(PVEBattleResultCsReq req, Battle battle) {
        super(CmdId.PVEBattleResultScRsp);
        
        // Item drop list data
        ItemList dropData = ItemList.newInstance();
        
        for (GameItem drop : battle.getDrops()) {
            dropData.addItemList(drop.toProto());
        }

        // Battle result
        var data = PVEBattleResultScRsp.newInstance()
                .setDropData(dropData)
                .setResVersion(Integer.toString(req.getClientResVersion()))
                .setBinVersion("")
                .setBattleId(req.getBattleId())
                .setStageId(req.getStageId())
                .setEndStatus(req.getEndStatus())
                .setCheckIdentical(true);

        // Set these
        data.getMutableUnk1();
        data.getMutableUnk2();
        data.getMutableUnk3();
        
        this.setData(data);
    }
}
