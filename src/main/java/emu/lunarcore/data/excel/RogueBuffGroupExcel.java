package emu.lunarcore.data.excel;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.rogue.RogueBuffData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@ResourceType(name = {"RogueBuffGroup.json"}, loadPriority = LoadPriority.LOW)
public class RogueBuffGroupExcel extends GameResource {
    private int JHOKDPADHFM;  // RogueBuffGroupID
    private List<Integer> ADJICNNJFEM;  // RogueBuffTagList or RogueBuffGroupList
    
    private transient Set<RogueBuffData> rogueBuffList = new HashSet<>();
    
    @Override
    public int getId() {
        return JHOKDPADHFM;
    }
    
    @Override
    public void onLoad() {
        for (int rogueTagId : ADJICNNJFEM) {
            if (rogueTagId >= 1000000 && rogueTagId <= 9999999) {
                var rogueBuff = GameData.getRogueBuffTagExcelMap().get(rogueTagId);
                if (rogueBuff != null) rogueBuffList.add(new RogueBuffData(rogueBuff.getMazeBuffID(), rogueBuff.getMazeBuffLevel()));
            } else {
                // RogueBuffGroup
                var rogueBuffGroup = GameData.getRogueBuffGroupExcelMap().get(rogueTagId);
                if (rogueBuffGroup != null) rogueBuffList.addAll(rogueBuffGroup.getRogueBuffList());
            }
        }
        GameData.getRogueBuffGroupExcelMap().put(JHOKDPADHFM, this);
    }

    @Override
    public void onFinalize() {
        for (int rogueTagId : ADJICNNJFEM) {
            if (rogueTagId >= 1000000 && rogueTagId <= 9999999) {
                var rogueBuff = GameData.getRogueBuffTagExcelMap().get(rogueTagId);
                if (rogueBuff != null) rogueBuffList.add(new RogueBuffData(rogueBuff.getMazeBuffID(), rogueBuff.getMazeBuffLevel()));
            } else {
                // RogueBuffGroup
                var rogueBuffGroup = GameData.getRogueBuffGroupExcelMap().get(rogueTagId);
                if (rogueBuffGroup == null) 
                    continue;
                
                if(rogueBuffGroup.getRogueBuffList().isEmpty()) {
                    rogueBuffGroup.onFinalize();
                }
                rogueBuffList.addAll(rogueBuffGroup.getRogueBuffList());
            }
        }
    }
}
