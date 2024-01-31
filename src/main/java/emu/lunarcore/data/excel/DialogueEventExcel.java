package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.game.enums.DialogueEventCostType;
import emu.lunarcore.game.enums.DialogueEventType;
import emu.lunarcore.game.rogue.RogueBuffType;
import lombok.Getter;

import java.util.List;

@Getter
@ResourceType(name = {"DialogueEvent.json"}, loadPriority = LoadPriority.LOW)
public class DialogueEventExcel extends GameResource {
    public int EventID;
    public DialogueEventType RogueEffectType;
    public List<Integer> RogueEffectParamList;
    public DialogueEventCostType CostType;
    public List<Integer> CostParamList;
    public List<Integer> ConditionIDList;
    public RogueBuffType AeonOption;
    @Override
    public int getId() {
        return EventID;
    }
    
    @Override
    public void onLoad() {
        GameData.getRogueDialogueEventList().put(EventID, this);
    }
}
