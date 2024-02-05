package emu.lunarcore.data.excel;

import com.google.gson.annotations.SerializedName;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.Getter;

import java.util.List;

@Getter
@ResourceType(name = { "RogueDLCArea.json" })
public class RogueDLCAreaExcel extends GameResource {
    private int AreaID;
    private String SubType;
    
    private List<Integer> DifficultyID;
    private List<Integer> LayerIDList;
    
    private List<RogueDLCAreaScoreMap> AreaScoreMap;
    
    @Getter
    public static class RogueDLCAreaScoreMap {
        @SerializedName("FJBAFMJHNCA") private int LayerID;
        @SerializedName("LHKHLPFAKGD") private int Score;
        @SerializedName("NDPIHCHCLGA") private int FinishedScore;
    }
    
    @Override
    public int getId() {
        return AreaID;
    }
    
    @Override
    public void onLoad() {
        GameData.getRogueDLCAreaExcelMap().put(AreaID, this);
    }
}
