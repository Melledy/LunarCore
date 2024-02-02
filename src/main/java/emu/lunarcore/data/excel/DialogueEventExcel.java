package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import emu.lunarcore.data.config.RogueDialogueEventInfo;
import emu.lunarcore.game.enums.DialogueEventCostType;
import emu.lunarcore.game.enums.DialogueEventType;
import emu.lunarcore.game.rogue.RogueBuffType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@ResourceType(name = {"DialogueEvent.json"})
public class DialogueEventExcel extends GameResource {
    public int EventID;
    public DialogueEventType RogueEffectType;
    public List<Integer> RogueEffectParamList;
    public DialogueEventCostType CostType;
    public List<Integer> CostParamList;
    public List<Integer> ConditionIDList;
    public RogueBuffType AeonOption;
    
    @Setter private RogueDialogueEventInfo info;
    @Override
    public int getId() {
        return EventID;
    }
    
    @Override
    public void onLoad() {
        GameData.getRogueDialogueEventList().put(EventID, this);
    }

    public String getJsonPath() {
        return "Config/Level/RogueDialogue/RogueDialogueEvent/Act/Act00" + this.getId() + ".json";
    }
    
    public String getSecondPath() {
        return "Config/Level/RogueDialogue/RogueDialogueEvent/Act/Act4038" + this.getId() + ".json";
    }
}
