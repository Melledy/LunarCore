package emu.lunarcore.data.excel;

import java.util.Map;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;

import lombok.Getter;

@Getter
@ResourceType(name = {"RogueRoom.json"})
public class RogueRoomExcel extends GameResource {
    private int RogueRoomID;
    private int RogueRoomType;
    private int MapEntrance;
    private int GroupID;
    private Map<Integer, Integer> GroupWithContent;
    
    @Override
    public int getId() {
        return RogueRoomID;
    }

}
