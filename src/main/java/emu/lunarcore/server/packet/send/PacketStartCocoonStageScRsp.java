package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.proto.StartCocoonStageScRspOuterClass.StartCocoonStageScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketStartCocoonStageScRsp extends BasePacket {

    public PacketStartCocoonStageScRsp() {
        super(CmdId.StartCocoonStageScRsp);
        
        var data = StartCocoonStageScRsp.newInstance()
                .setRetcode(1);
        
        this.setData(data);
    }
    
    public PacketStartCocoonStageScRsp(Battle battle, int cocoonId, int wave) {
        super(CmdId.StartCocoonStageScRsp);
        
        var data = StartCocoonStageScRsp.newInstance()
                .setBattleInfo(battle.toProto())
                .setCocoonId(cocoonId)
                .setWave(wave);
        
        this.setData(data);
    }
}
