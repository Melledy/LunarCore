package emu.lunarcore.data.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.triggers.TriggerOpenTreasureWhenMonsterDie;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

/**
 *  Original name: LevelFloorInfo
 */
@Getter
public class FloorInfo {
    private int FloorID;
    private int StartGroupIndex;
    private int StartAnchorID;
    
    private List<FloorDimensionInfo> DimensionList;
    private Map<Integer, GroupInstanceCommonInfo> GroupInstanceCommonMap;
    
    @SerializedName(value = "GroupInstanceList")
    private List<FloorGroupSimpleInfo> SimpleGroupList;

    // Cached data
    private transient boolean loaded;
    private transient List<ExtraDataInfo> savedValues;
    private transient Int2ObjectMap<GroupInfo> groups;
    private transient Int2ObjectMap<PropInfo> cachedTeleports;
    private transient List<PropInfo> unlockedCheckpoints; // DEBUG
    
    public FloorInfo() {
        this.groups = new Int2ObjectLinkedOpenHashMap<>();
        this.cachedTeleports = new Int2ObjectOpenHashMap<>();
        this.unlockedCheckpoints = new ArrayList<>();
    }
    
    public GroupInfo getGroupInfoByIndex(int groupIndex) {
        var simpleGroup = SimpleGroupList.get(groupIndex);
        return this.getGroups().get(simpleGroup.getID());
    }
    
    public AnchorInfo getStartAnchorInfo() {
        GroupInfo group = this.getGroupInfoByIndex(StartGroupIndex);
        if (group == null) return null;
        
        return getAnchorInfo(group, StartAnchorID);
    }
    
    public FloorDimensionInfo getMainDimension() {
        if (this.getDimensionList() == null || this.getDimensionList().isEmpty()) {
            return null;
        }
        
        return this.getDimensionList().stream()
                .filter(d -> d.getID() == 0)
                .findFirst()
                .orElse(null);
    }

    public List<ExtraDataInfo> getExtraDatas() {
        if (this.savedValues == null) {
            this.savedValues = new ArrayList<>();
            
            var dimension = this.getMainDimension();
            if (dimension != null && dimension.getSavedValues() != null) {
                this.savedValues.addAll(dimension.getSavedValues());
            }
        }
        
        return this.savedValues;
    }
    
    public AnchorInfo getAnchorInfo(int groupId, int anchorId) {
        GroupInfo group = this.getGroups().get(groupId);
        if (group == null) return null;
        
        return getAnchorInfo(group, anchorId);
    }
    
    public AnchorInfo getAnchorInfo(GroupInfo group, int anchorId) {
        return group.getAnchorList().stream().filter(a -> a.getID() == anchorId).findFirst().orElse(null);
    }
    
    public void onLoad() {
        if (this.loaded) return;

        this.savedValues = getExtraDatas();
        
        // Cache anchors
        for (GroupInfo group : groups.values()) {
            if (group.getPropList() == null) {
                continue;
            }
            
            for (PropInfo prop : group.getPropList()) {
                // Set parent group for prop
                prop.setGroup(group);
                
                // Check if prop can be teleported to

                //TODO: better fix mission 100010105
                // AlreadyInPerformance Trigger D-100010104 Running D-100010104
                if(prop.getPropID() == 100000){
                    prop.setState(PropState.BridgeState3);
                }

                if (prop.getAnchorID() > 0) {
                    // Put inside cached teleport list to send to client when they request map info
                    this.cachedTeleports.put(prop.getMappingInfoID(), prop);
                    this.unlockedCheckpoints.add(prop);
                    
                    // Force prop to be in the unlocked state
                    prop.setState(PropState.CheckPointEnable);
                } else if (prop.getInitLevelGraph() != null) {
                    String json = prop.getInitLevelGraph();
                    
                    // Hacky way to setup prop triggers
                    if (json.contains("Maze_GroupProp_OpenTreasure_WhenMonsterDie")) {
                        prop.setTrigger(new TriggerOpenTreasureWhenMonsterDie(group.getId()));
                    } else if (json.contains("Common_Console")) {
                        prop.setCommonConsole(true);
                    }
                    
                    // Clear for garbage collection
                    prop.setValueSource(null);
                    prop.setInitLevelGraph(null);
                }
            }
        }
        
        this.loaded = true;
    }
    
    @Getter
    public static class FloorDimensionInfo {
        private int ID;
        private List<ExtraDataInfo> SavedValues;
        private IntArrayList GroupIndexList;
    }

    @Getter
    public static class ExtraDataInfo {
        private int ID;
        private String Name;
        private int DefaultValue;
        private List<Integer> AllowedValues;
        private int MaxValue;
        private Integer MinValue;
    }
    
    @Getter
    public static class FloorGroupSimpleInfo {
        private String GroupPath;
        private String Name;
        private boolean IsDelete;
        private int ID;
        
        public String getName() {
            if (this.Name == null) {
                return "";
            }
            
            return this.Name;
        }
    }
    
    @Getter
    public static class GroupInstanceCommonInfo {
        private boolean IsDelete;
        private GroupCategory Category;
    }
    
    public enum GroupCategory {
        Normal, Mission, BattleProps, BattleAudiences, Custom, System, Atmosphere;
    }
}
