package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.rogue.RogueBuffData;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

@Getter
@ResourceType(name = {"RogueBuffGroup.json"}, loadPriority = LoadPriority.LOW)
public class RogueBuffGroupExcel extends GameResource {
    @SerializedName(value = "GJHLAKLLFDI")
    private int RogueBuffGroupID;  // RogueBuffGroupID
    
    @SerializedName(value = "DNKFBOAIDCE")
    private IntArrayList RogueBuffTagList;  // RogueBuffTagList or RogueBuffGroupList
    
    private transient Set<RogueBuffData> rogueBuffList = new HashSet<>();
    
    @Override
    public int getId() {
        return RogueBuffGroupID;
    }
    
    @Override
    public void onLoad() {
        for (int rogueTagId : RogueBuffTagList) {
            if (rogueTagId >= 1000000 && rogueTagId <= 9999999) {
                var rogueBuff = GameData.getRogueBuffTagExcelMap().get(rogueTagId);
                if (rogueBuff != null) rogueBuffList.add(new RogueBuffData(rogueBuff.getMazeBuffID(), rogueBuff.getMazeBuffLevel()));
            } else {
                // RogueBuffGroup
                var rogueBuffGroup = GameData.getRogueBuffGroupExcelMap().get(rogueTagId);
                if (rogueBuffGroup != null) rogueBuffList.addAll(rogueBuffGroup.getRogueBuffList());
            }
        }
        GameData.getRogueBuffGroupExcelMap().put(RogueBuffGroupID, this);
    }

    @Override
    public void onFinalize() {
        for (int rogueTagId : RogueBuffTagList) {
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
