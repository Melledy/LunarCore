package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueNPC.json"})
public class RogueNPCExcel extends GameResource {
    private int RogueNPCID;
    private int NPCID;

    @Override
    public int getId() {
        return RogueNPCID;
    }

    @Override
    public void onLoad() {
        if (NPCID == 3013 && RogueNPCID >= 10000 && RogueNPCID <= 19999) {
            GameDepot.getRogueRandomNpcList().add(this);
        }
    }
}
