package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import lombok.Getter;

@Getter
@ResourceType(name = {"SpecialAvatar.json"})
public class SpecialAvatarExcel extends GameResource {
    private int SpecialAvatarID;
    private int PlayerID;
    private int AvatarID;
    private int Level;
    private int Promotion;
    private int Rank;

    private int EquipmentID;
    private int EquipmentLevel;
    private int EquipmentPromotion;
    private int EquipmentRank;

    @Override
    public int getId() {
        return SpecialAvatarID;
    }
}
