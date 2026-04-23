package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.QuickStartFarmElementScRspOuterClass.QuickStartFarmElementScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketQuickStartFarmElementScRsp extends BasePacket {

    public PacketQuickStartFarmElementScRsp(Battle battle) {
        super(CmdId.QuickStartFarmElementScRsp);

        var data = QuickStartFarmElementScRsp.newInstance();

        if (battle != null) {
            data.setBattleInfo(battle.toProto())
            .setFarmElementId(battle.getMappingInfoId());
        } else {
            data.setRetcode(1);
        }

        this.setData(data);
    }
}
