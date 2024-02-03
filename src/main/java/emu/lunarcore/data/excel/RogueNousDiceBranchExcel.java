package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

import java.util.Set;

@Getter
@ResourceType(name = {"RogueNousDiceBranch.json"})
public class RogueNousDiceBranchExcel extends GameResource {
    private int BranchId;
    private int DefaultUltraSurface;
    private Set<Integer> DefaultCommonSurfaceList;
    
    @Override
    public int getId() {
        return BranchId;
    }
    
    @Override
    public void onLoad() {
        GameData.getRogueNousDiceBranchExcelMap().put(BranchId, this);
    }
}
