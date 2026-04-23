package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
@ResourceType(name = {"CocoonConfig.json"})
public class CocoonExcel extends MultiKeyGameResource {
    private int ID;
    private int MappingInfoID;
    private int WorldLevel;
    private int PropID;
    private int StaminaCost;
    private int MaxChallengeCnt;
    private IntArrayList StageIDList;
    private IntArrayList DropList;

    @Override
    public int getPrimaryKey() {
        return ID;
    }

    @Override
    public int getSecondaryKey() {
        return WorldLevel;
    }

    public int getCocoonId() {
        return ID;
    }

    public int getRandomStage() {
        return Utils.randomElement(StageIDList);
    }
}
