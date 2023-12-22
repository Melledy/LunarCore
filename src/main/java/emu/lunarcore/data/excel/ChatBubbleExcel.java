package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.enums.PersonalizeShowType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ChatBubbleConfig.json"}, loadPriority = LoadPriority.LOW)
public class ChatBubbleExcel extends GameResource {
    private int ID;
    private PersonalizeShowType ShowType;

    @Override
    public int getId() {
        return ID;
    }

}
