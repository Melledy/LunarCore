package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.StartCocoonStageScRspOuterClass.StartCocoonStageScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketStartCocoonStageScRsp extends BasePacket {

    public PacketStartCocoonStageScRsp(Battle battle) {
        super(CmdId.StartCocoonStageScRsp);

        var data = StartCocoonStageScRsp.newInstance();

        if (battle != null) {
            data.setBattleInfo(battle.toProto())
            .setCocoonId(battle.getMappingInfoId())
            .setWave(battle.getCocoonWave());
        } else {
            data.setRetcode(1);
        }


        this.setData(data);
    }
}
