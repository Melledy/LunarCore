package emu.lunarcore.game.rogue;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueMiracleExcel;
import emu.lunarcore.proto.RogueMiracleSelectInfoOuterClass.RogueMiracleSelectInfo;
import emu.lunarcore.util.WeightedList;
import lombok.Getter;

@Getter
public class RogueMiracleSelectMenu {
    private transient RogueInstance rogue;
    
    private int maxMiracles;
    private List<RogueMiracleData> miracles;
    
    // Cache
    private transient WeightedList<RogueMiracleExcel> randomMiracles;
    
    @Deprecated // Morphia only!
    public RogueMiracleSelectMenu() {}
    
    public RogueMiracleSelectMenu(RogueInstance rogue) {
        this.rogue = rogue;
        this.maxMiracles = 3;
        this.miracles = new ArrayList<>();
        
        this.generateRandomBuffs();
    }
    
    public void generateRandomBuffs() {
        if (this.randomMiracles == null) {
            this.randomMiracles = new WeightedList<>();
            
            for (var excel : GameDepot.getRogueRandomMiracleList()) {
                if (rogue.getBuffs().containsKey(excel.getMiracleID())) {
                    continue;
                }
                
                this.randomMiracles.add(1.0, excel);
            };
        }
        
        this.getMiracles().clear();
        
        while (this.getMiracles().size() < this.getMaxMiracles()) {
            var excel = this.randomMiracles.next();
            this.getMiracles().add(new RogueMiracleData(excel.getMiracleID()));
        }
    }
    
    protected void onLoad(RogueInstance rogue) {
        this.rogue = rogue;
    }
    
    public RogueMiracleSelectInfo toProto() {
        var proto = RogueMiracleSelectInfo.newInstance();

        for (var miracle : this.getMiracles()) {
            proto.addAllMiracleIdList(miracle.getId());
        }
        
        return proto;
    }
}
