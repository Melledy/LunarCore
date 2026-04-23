package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"PlaneEvent.json"})
public class PlaneEventExcel extends MultiKeyGameResource {
    private int EventID;
    private int WorldLevel;
    private int StageID;

    @Override
    public int getPrimaryKey() {
        return EventID;
    }

    @Override
    public int getSecondaryKey() {
        return WorldLevel;
    }

    public int getEventId() {
        return EventID;
    }

    public int getStageId() {
        return StageID;
    }

}