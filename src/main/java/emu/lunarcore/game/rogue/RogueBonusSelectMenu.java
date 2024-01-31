package emu.lunarcore.game.rogue;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.excel.RogueBonusExcel;
import emu.lunarcore.proto.RogueBonusSelectInfoOuterClass.RogueBonusSelectInfo;
import emu.lunarcore.util.WeightedList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RogueBonusSelectMenu {
    private transient RogueInstance rogue;
    
    private int maxBonuses;
    private List<RogueBonusData> bonuses;

    // Cache
    private transient WeightedList<RogueBonusExcel> randomBonuses;

    public RogueBonusSelectMenu(RogueInstance rogue) {
        this.rogue = rogue;
        this.maxBonuses = 3;
        this.bonuses = new ArrayList<>();
        
        this.generateRandomBonuses();
    }
    
    public void generateRandomBonuses() {
        if (this.randomBonuses == null) {
            this.randomBonuses = new WeightedList<>();
            for (var excel : GameDepot.getRogueRandomCommonBonusList()) {
                this.randomBonuses.add(1.0, excel);
            };
        }
        
        this.getBonuses().clear();
        
        while (this.getBonuses().size() < this.getMaxBonuses()) {
            var excel = this.randomBonuses.next();
            this.getBonuses().add(new RogueBonusData(excel.getBonusID(), excel.getBonusEvent()));
        }
    }
    
    public RogueBonusSelectInfo toProto() {
        var proto = RogueBonusSelectInfo.newInstance();
        
        for (var bonus : this.getBonuses()) {
            proto.addBonusInfo(bonus.getId());
        }
        
        return proto;
    }
}
