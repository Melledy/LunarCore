package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.SceneEnterStageScRspOuterClass.SceneEnterStageScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSceneEnterStageScRsp extends BasePacket {

    public PacketSceneEnterStageScRsp(Battle battle) {
        super(CmdId.SceneEnterStageScRsp);

        var proto = SceneEnterStageScRsp.newInstance();

        if (battle != null) {
            proto.setBattleInfo(battle.toProto());
        } else {
            proto.setRetcode(1);
        }

        this.setData(proto);
    }
}
