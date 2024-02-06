package emu.lunarcore.game.rogue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueBuffExcel;
import emu.lunarcore.proto.ItemCostListOuterClass.ItemCostList;
import emu.lunarcore.proto.ItemCostOuterClass.ItemCost;
import emu.lunarcore.proto.PileItemOuterClass.PileItem;
import emu.lunarcore.proto.RogueCommonBuffSelectInfoOuterClass.RogueCommonBuffSelectInfo;
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
    private transient Set<RogueBuffData> allRandomBuffs;
    
    @Deprecated // Morphia only!
    public RogueBuffSelectMenu() {}
    
    public RogueBuffSelectMenu(RogueInstance rogue) {
        this(rogue, false, GameData.getRogueBuffGroupExcelMap().get(110002).getRogueBuffList());
    }
    
    public RogueBuffSelectMenu(RogueInstance rogue, boolean generateAeonBuffs, Set<RogueBuffData> buffs) {
        this.rogue = rogue;
        this.maxBuffs = 3;
        this.maxRerolls = rogue.getBaseRerolls();
        this.buffs = new ArrayList<>();
        this.allRandomBuffs = buffs;
        
        if (generateAeonBuffs) {
            this.generateAeonBuffs();
        } else {
            this.generateRandomBuffs();
        }
    }
    
    public RogueBuffSelectMenu(RogueInstance rogue, boolean generateAeonBuffs) {
        this(rogue, generateAeonBuffs, new HashSet<>());
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
            
            for (var excel : this.getAllRandomBuffs()) {
                if (rogue.getBuffs().containsKey(excel.getExcel().getMazeBuffID())) {
                    continue;
                }
                
                // Calculate buff weights
                double weight = 10.0 / excel.getExcel().getRogueBuffRarity();
                
                if (getRogue().getAeonBuffType() == excel.getExcel().getRogueBuffType()) {
                    weight *= 2;
                }
                
                this.randomBuffs.add(weight, excel.getExcel());
            };
        }
        
        this.getBuffs().clear();
        
        while (this.getBuffs().size() < this.getMaxBuffs()) {
            var excel = this.randomBuffs.next();
            this.getBuffs().add(new RogueBuffData(excel.getMazeBuffID(), 1));
        }
        
        this.hint += 1;
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
    
    public RogueCommonBuffSelectInfo toProto() {
        var proto = RogueCommonBuffSelectInfo.newInstance()
                .setSelectBuffSourceHint(this.getHint())
                .setSourceCurCount(1)
                .setSourceTotalCount(1);
        
        if (this.getMaxRerolls() > 0) {
            proto.setCanRoll(true);
            proto.setRollBuffTimes(this.getRerolls());
            proto.setRollBuffMaxTimes(this.getMaxRerolls());
        }
        
        for (var buff : this.getBuffs()) {
            proto.addMazeBuffList(buff.toCommonProto());
            proto.addHandbookUnlockBuffIdList(buff.getId());
        }
        
        // Create item list for reroll cost
        proto.setRollBuffsCost(ItemCostList.newInstance()
            .addItemList(ItemCost.newInstance()
                .setPileItem(PileItem.newInstance()
                    .setItemId(31)
                    .setItemNum(30))));
        
        return proto;
    }
}
