package emu.lunarcore.server.packet.send;

import emu.lunarcore.proto.ItemListOuterClass.ItemList;
import emu.lunarcore.proto.PVEBattleResultCsReqOuterClass.PVEBattleResultCsReq;
import emu.lunarcore.proto.PVEBattleResultScRspOuterClass.PVEBattleResultScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketPVEBattleResultScRsp extends BasePacket {

    public PacketPVEBattleResultScRsp(PVEBattleResultCsReq req) {
        super(CmdId.PVEBattleResultScRsp);

        var data = PVEBattleResultScRsp.newInstance()
                .setUnk1(ItemList.newInstance())
                .setUnk2(ItemList.newInstance())
                .setUnk3(ItemList.newInstance())
                .setResVersion(Integer.toString(req.getClientResVersion()))
                .setBinVersion("")
                .setBattleId(req.getBattleId())
                .setStageId(req.getStageId())
                .setEndStatus(req.getEndStatus())
                .setCheckIdentical(true);

        this.setData(data);
    }
}
