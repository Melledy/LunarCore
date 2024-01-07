package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
@ResourceType(name = {"CocoonConfig.json"})
public class CocoonExcel extends GameResource {
    private int ID;
    private int MappingInfoID;
    private int WorldLevel;
    private int PropID;
    private int StaminaCost;
    private int MaxWave;
    private IntArrayList StageIDList;
    private IntArrayList DropList;

    @Override
    public int getId() {
        return (ID << 8) + WorldLevel;
    }
    
    public int getCocoonId() {
        return ID;
    }

    public int getRandomStage() {
        return Utils.randomElement(StageIDList);
    }
}
