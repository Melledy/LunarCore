package emu.lunarcore.data.excel;

import java.util.Set;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.enums.PropType;
import lombok.Getter;

@Getter
@ResourceType(name = {"MazeProp.json"})
public class PropExcel extends GameResource {
    private int ID;
    private long PropName;
    private String JsonPath;
    private PropType PropType;
    private Set<PropState> PropStateList;

    private transient boolean recoverHp;
    private transient boolean recoverMp;
    private transient boolean isDoor;
    
    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void onLoad() {
        // Hacky way to determine if a prop will recover hp or mp
        if (getJsonPath() != null && getJsonPath().length() > 0) {
            if (getJsonPath().contains("MPRecover") || getJsonPath().contains("MPBox")) {
                this.recoverMp = true;
            } else if (getJsonPath().contains("HPRecover") || getJsonPath().contains("HPBox")) {
                this.recoverHp = true;
            } else if (getJsonPath().contains("_Door_")) {
                this.isDoor = true;
            }
        }
        
        // Sanity
        if (this.PropStateList == null) {
            this.PropStateList = Set.of();
        }

        // Clear for optimization
        this.JsonPath = null;
    }
}
