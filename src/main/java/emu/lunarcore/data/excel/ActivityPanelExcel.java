package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ActivityPanel.json"})
public class ActivityPanelExcel extends GameResource {
    private int PanelID;
    private int Type;
    private int ActivityModuleID;
    private boolean IsResidentPanel;

    @Override
    public int getId() {
        return PanelID;
    }
}
