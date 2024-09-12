package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.config.rogue.RogueNPCConfigInfo;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@ResourceType(name = {"RogueNPC.json"})
public class RogueNPCExcel extends GameResource {
    public int RogueNPCID;
    public String NPCJsonPath;

    @Setter
    @Nullable
    public RogueNPCConfigInfo RogueNpcConfig;

    @Override
    public int getId() {
        return RogueNPCID;
    }
}
