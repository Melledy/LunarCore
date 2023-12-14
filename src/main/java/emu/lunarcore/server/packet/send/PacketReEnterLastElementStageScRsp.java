package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.ReEnterLastElementStageScRspOuterClass.ReEnterLastElementStageScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketReEnterLastElementStageScRsp extends BasePacket {

    public PacketReEnterLastElementStageScRsp() {
        super(CmdId.ReEnterLastElementStageScRsp);
        
        var data = ReEnterLastElementStageScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }
    
    public PacketReEnterLastElementStageScRsp(Battle battle) {
        super(CmdId.ReEnterLastElementStageScRsp);
        
        var data = ReEnterLastElementStageScRsp.newInstance()
                .setStageId(battle.getStage().getId())
                .setBattleInfo(battle.toProto());
        
        this.setData(data);
    }
}
