package emu.lunarcore.data.excel;

import java.util.Comparator;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarExpItemConfig.json"}, loadPriority = LoadPriority.LOW)
public class AvatarExpItemExcel extends GameResource {
    private int ItemID;
    private int Exp;

    @Override
    public int getId() {
        return ItemID;
    }

    @Override
    public void onLoad() {
        // Set exp for this item
        ItemExcel excel = GameData.getItemExcelMap().get(ItemID);
        if (excel == null) return;

        excel.setAvatarExp(Exp);

        // Add to game depot
        if (Exp > 0) {
            GameDepot.getAvatarExpExcels().add(this);
            GameDepot.getAvatarExpExcels().sort(new Comparator<AvatarExpItemExcel>() {
                @Override
                public int compare(AvatarExpItemExcel o1, AvatarExpItemExcel o2) {
                    return o2.getExp() - o1.getExp();
                }
            });
        }
    }
}
