package emu.lunarcore.data.excel;

import java.util.Set;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import emu.lunarcore.data.resource.ResourceType.LoadPriority;
import emu.lunarcore.game.enums.AvatarPropertyType;
import lombok.Getter;

@Getter
@ResourceType(name = {"AvatarRelicRecommend.json", "AvatarRelicRecommendLD.json"}, loadPriority = LoadPriority.LOW)
public class AvatarRelicRecommendExcel extends GameResource {
    private int AvatarID;
    private int[] Set4IDList;
    private int[] Set2IDList;
    
    private AvatarPropertyType[] PropertyList3;
    private AvatarPropertyType[] PropertyList4;
    private AvatarPropertyType[] PropertyList5;
    private AvatarPropertyType[] PropertyList6;
    
    private Set<AvatarPropertyType> SubAffixPropertyList;
    
    @Override
    public int getId() {
        return AvatarID;
    }
    
    public int getMainSetId() {
        return this.Set4IDList[0];
    }
    
    public int getPlanarSetId() {
        return this.Set2IDList[0];
    }
    
    public AvatarPropertyType getProp3() {
        return this.PropertyList3[0];
    }
    
    public AvatarPropertyType getProp4() {
        return this.PropertyList4[0];
    }
    
    public AvatarPropertyType getProp5() {
        return this.PropertyList5[0];
    }
    
    public AvatarPropertyType getProp6() {
        return this.PropertyList6[0];
    }
}
