package emu.lunarcore.data.excel;

import com.google.gson.annotations.SerializedName;

import emu.lunarcore.data.resource.MultiKeyGameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import lombok.Getter;

@Getter
@ResourceType(name = {"EquipmentExpType.json"}, loadPriority = LoadPriority.NORMAL)
public class EquipmentExpTypeExcel extends MultiKeyGameResource {
    @SerializedName(value = "id", alternate = {"ExpType"})
    private int TypeID;
    private int Level;
    private int Exp;

    @Override
    public int getPrimaryKey() {
        return TypeID;
    }

    @Override
    public int getSecondaryKey() {
        return Level;
    }

    @Override
    public void onLoad() {

    }
}
