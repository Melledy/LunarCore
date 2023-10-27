package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;

import lombok.Getter;

@Getter
@ResourceType(name = {"RogueBuff.json"})
public class RogueBuffExcel extends GameResource {
    private int MazeBuffID;
    private int MazeBuffLevel;
    private int RogueBuffType;
    private int RogueBuffRarity;
    private int AeonID;
    
    @Override
    public int getId() {
        return MazeBuffID;
    }

    @Override
    public void onLoad() {
        if (RogueBuffType >= 120 && RogueBuffType <= 126 && RogueBuffRarity >= 1 && RogueBuffRarity <= 3 && MazeBuffLevel == 1 && AeonID == 0) {
            GameDepot.getRogueRandomBuffList().add(this);
        }
    }
}
