package emu.lunarcore.data.excel;

import com.google.gson.annotations.SerializedName;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.data.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"EquipmentExpType.json"}, loadPriority = LoadPriority.NORMAL)
public class EquipmentExpTypeExcel extends GameResource {
    @SerializedName(value = "id", alternate = {"ExpType"})
    private int TypeID;
    private int Level;
    private int Exp;

    @Override
    public int getId() {
        return (TypeID << 16) + Level;
    }

    @Override
    public void onLoad() {

    }
}
