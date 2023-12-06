package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChatBubbleConfig.json"}, loadPriority = LoadPriority.LOW)
public class ChatBubbleExcel extends GameResource {
    private int ID;
    private String ShowType;
    private int ShowParam;

    @Override
    public int getId() {
        return ID;
    }

}
