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
    private int hint;
    private List<RogueBuffData> buffs;
    
    // Cache
    private transient WeightedList<RogueBuffExcel> randomBuffs;
    
    @Deprecated // Morphia only!
    public RogueBuffSelectMenu() {}
    
    public RogueBuffSelectMenu(RogueInstance rogue) {
        this(rogue, false);
    }
    
    public RogueBuffSelectMenu(RogueInstance rogue, boolean generateAeonBuffs) {
        this.rogue = rogue;
        this.maxBuffs = 3;
        this.maxRerolls = rogue.getBaseRerolls();
        this.buffs = new ArrayList<>();
        
        if (generateAeonBuffs) {
            this.generateAeonBuffs();
        } else {
            this.generateRandomBuffs();
        }
    }
    
    public void setMaxRerolls(int i) {
        this.maxBuffs = i;
    }
    
    public void reroll() {
        this.generateRandomBuffs();
        this.rerolls++;
    }
    
    public boolean hasRerolls() {
        return this.maxRerolls > this.rerolls;
    }
    
    private void generateRandomBuffs() {
        if (this.randomBuffs == null) {
            this.randomBuffs = new WeightedList<>();
            
            for (var excel : GameDepot.getRogueRandomBuffList()) {
                if (rogue.getBuffs().containsKey(excel.getMazeBuffID())) {
                    continue;
                }
                
                // Calculate buff weights
                double weight = 10.0 / excel.getRogueBuffRarity();
                
                if (getRogue().getAeonBuffType() == excel.getRogueBuffType()) {
                    weight *= 2;
                }
                
                this.randomBuffs.add(weight, excel);
            };
        }
        
        this.getBuffs().clear();
        
        while (this.getBuffs().size() < this.getMaxBuffs()) {
            var excel = this.randomBuffs.next();
            this.getBuffs().add(new RogueBuffData(excel.getMazeBuffID(), 1));
        }
    }
    
    private void generateAeonBuffs() {
        this.getBuffs().clear();
        
        var aeonBuffExcel = GameDepot.getRogueAeonBuffs().get(getRogue().getAeonId());
        if (aeonBuffExcel == null) return;
        
        // Select buff menu hint
        this.hint = (getRogue().getAeonId() * 100) + 1;
        
        // Check for rogue aeon buffs
        if (!this.getRogue().getBuffs().containsKey(aeonBuffExcel.getMazeBuffID())) {
            // We dont have the first aeon buff yet
            this.getBuffs().add(new RogueBuffData(aeonBuffExcel.getMazeBuffID(), 1));
        } else {
            // Add hint
            this.hint += 1;
            // Add path resonances that we currently dont have
            for (var aeonEnhanceExcel : GameDepot.getRogueAeonEnhanceBuffs().get(getRogue().getAeonId())) {
                if (!this.getRogue().getBuffs().containsKey(aeonEnhanceExcel.getMazeBuffID())) {
                    this.getBuffs().add(new RogueBuffData(aeonEnhanceExcel.getMazeBuffID(), 1));
                } else {
                    this.hint += 1;
                }
            }
        }
    }
    
    protected void onLoad(RogueInstance rogue) {
        this.rogue = rogue;
    }
    
    public RogueBuffSelectInfo toProto() {
        var proto = RogueBuffSelectInfo.newInstance()
                .setSelectBuffSourceHint(this.getHint());
        
        if (this.getMaxRerolls() > 0) {
            proto.setCanRoll(true);
            proto.setRollBuffTimes(this.getRerolls());
            proto.setRollBuffMaxTimes(this.getMaxRerolls());
        }
        
        for (var buff : this.getBuffs()) {
            proto.addMazeBuffList(buff.toProto());
        }
        
        // Create item list for reroll cost
        proto.getMutableRollBuffsCost();
        
        return proto;
    }
}
