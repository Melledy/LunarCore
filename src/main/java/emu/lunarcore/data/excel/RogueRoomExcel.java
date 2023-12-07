package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueRoom.json"})
public class RogueRoomExcel extends GameResource {
    private int RogueRoomID;
    private int RogueRoomType;
    private int MapEntrance;
    private int GroupID;
    private Int2IntOpenHashMap GroupWithContent;

    @Override
    public int getId() {
        return RogueRoomID;
    }

    public int getGroupContent(int id) {
        return GroupWithContent.get(id);
    }

}
