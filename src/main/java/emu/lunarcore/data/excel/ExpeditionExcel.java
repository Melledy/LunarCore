package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"ExpeditionData.json"})
public class ExpeditionExcel extends GameResource {
    private int[] AssignerIDList;
    private int ExpeditionID;
    private int GroupID;

    @Override
    public int getId() {
        return ExpeditionID;
    }

    @Override
    public void onLoad() {

    }
}
