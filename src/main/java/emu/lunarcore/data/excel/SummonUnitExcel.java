package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.config.SummonUnitInfo;
import lombok.Getter;

@Getter
@ResourceType(name = {"SummonUnitData.json"})
public class SummonUnitExcel extends GameResource {
    private int ID;
    private String JsonPath;
    private boolean IsClient;
    
    private transient SummonUnitInfo info;
    
    @Override
    public int getId() {
        return ID;
    }

    public void setInfo(SummonUnitInfo info) {
        if (this.info == null && !this.IsClient) {
            this.info = info;
        }
    }

}
