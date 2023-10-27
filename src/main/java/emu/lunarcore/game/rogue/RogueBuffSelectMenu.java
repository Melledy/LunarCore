package emu.lunarcore.game.rogue;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueBuffExcel;
import emu.lunarcore.proto.RogueBuffSelectInfoOuterClass.RogueBuffSelectInfo;
import emu.lunarcore.util.WeightedList;
import lombok.Getter;

@Getter
public class RogueBuffSelectMenu {
    private transient RogueInstance rogue;
    
    private int maxBuffs;
    private int rerolls;
    private int maxRerolls;
    private List<RogueBuffData> buffs;
    
    // Cache
    private transient WeightedList<RogueBuffExcel> randomBuffs;
    
    @Deprecated // Morphia only!
    public RogueBuffSelectMenu() {}
    
    public RogueBuffSelectMenu(RogueInstance rogue) {
        this.rogue = rogue;
        this.maxBuffs = 3;
        this.buffs = new ArrayList<>();
        
        this.generateRandomBuffs();
    }
    
    public void generateRandomBuffs() {
        if (this.randomBuffs == null) {
            this.randomBuffs = new WeightedList<>();
            
            for (RogueBuffExcel excel : GameDepot.getRogueBuffsList()) {
                if (rogue.getBuffs().containsKey(excel.getMazeBuffID())) {
                    continue;
                }
                
                this.randomBuffs.add(10.0 / excel.getRogueBuffRarity(), excel);
            };
        }
        
        this.getBuffs().clear();
        
        while (this.getBuffs().size() < this.getMaxBuffs()) {
            RogueBuffExcel excel = this.randomBuffs.next();
            this.getBuffs().add(new RogueBuffData(excel.getMazeBuffID(), 1));
        }
    }
    
    protected void onLoad(RogueInstance rogue) {
        this.rogue = rogue;
    }
    
    public RogueBuffSelectInfo toProto() {
        var proto = RogueBuffSelectInfo.newInstance();
        
        if (this.getMaxRerolls() > 0) {
            proto.setCanRoll(true);
            proto.setRollBuffTimes(this.getRerolls());
            proto.setRollBuffMaxTimes(this.getMaxRerolls());
        }
        
        for (var buff : this.getBuffs()) {
            proto.addMazeBuffList(buff.toProto());
        }
        
        proto.getMutableRollBuffsCost();
        
        return proto;
    }
}
