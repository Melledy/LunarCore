package emu.lunarcore.game.rogue;

import emu.lunarcore.proto.RogueMiracleDataInfoOuterClass.RogueMiracleDataInfo;
import emu.lunarcore.proto.RogueMiracleDataOuterClass;
import emu.lunarcore.proto.RogueMiracleOuterClass.RogueMiracle;
import lombok.Getter;

@Getter
public class RogueMiracleData {
    private int id;
    private boolean active;
    
    public RogueMiracleData(int miracleId) {
        this.id = miracleId;
        this.active = true;
    }
    
    public RogueMiracle toProto() {
        var proto = RogueMiracle.newInstance()
                .setMiracleId(this.getId());
        
        return proto;
    }
    
    public RogueMiracleDataInfo toInfoProto() {
        var proto = RogueMiracleDataInfo.newInstance()
                .setMiracleId(this.getId());
        
        return proto;
    }
    
    public RogueMiracleDataOuterClass.RogueMiracleData toDataProto() {
        var proto = RogueMiracleDataOuterClass.RogueMiracleData.newInstance();
        proto.setRogueMiracle(this.toProto());
        
        return proto;
    }
}
