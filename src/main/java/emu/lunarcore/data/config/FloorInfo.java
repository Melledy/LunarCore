package emu.lunarcore.data.config;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import lombok.Getter;

@Getter
public class FloorInfo {
    private int FloorID;
    @SerializedName(value = "GroupList")
    private List<FloorGroupSimpleInfo> SimpleGroupList;
    
    // Cached data
    private transient boolean loaded;
    private transient Int2ObjectMap<GroupInfo> groups;
    private transient Int2ObjectMap<PropInfo> cachedTeleports;
    
    public FloorInfo() {
        this.groups = new Int2ObjectOpenHashMap<>();
        this.cachedTeleports = new Int2ObjectOpenHashMap<>();
    }
    
    public AnchorInfo getAnchorInfo(int groupId, int anchorId) {
        GroupInfo group = this.getGroups().get(groupId);
        if (group == null) return null;
        
        return group.getAnchorList().stream().filter(a -> a.getID() == anchorId).findFirst().orElse(null);
    }
    
    public void onLoad() {
        if (this.loaded) return;
        
        // Cache anchors
        for (GroupInfo group : groups.values()) {
            if (group.getPropList() == null) {
                continue;
            }
            
            for (PropInfo prop : group.getPropList()) {
                if (prop.getAnchorID() > 0) {
                    this.cachedTeleports.put(prop.getMappingInfoID(), prop);
                }
            }
        }
        
        this.loaded = true;
    }
    
    @Getter
    public static class FloorGroupSimpleInfo {
        private String GroupPath;
        private int ID;
    }
}
