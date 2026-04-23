package emu.lunarcore.data.config;

import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 *  Original name: LevelGroupInfo
 */
@Getter
public class GroupInfo {
    private String GroupName;
    private transient int id;
    private GroupLoadSide LoadSide = GroupLoadSide.Server;
    private GroupCategory Category;
    private boolean LoadOnInitial = true;
    private int OwnerMainMissionID;

    private LoadConditionInfo AtmosphereCondition;
    private LoadConditionInfo LoadCondition;
    private LoadConditionInfo UnloadCondition;
    
    private Map<Integer, GroupProperty> GroupPropertyMap;
    
    private List<AnchorInfo> AnchorList;
    private List<MonsterInfo> MonsterList;
    private List<PropInfo> PropList;
    private List<NpcInfo> NPCList;
    
    public void setId(int id) {
        if (this.id == 0) this.id = id;
    }
    
    public String getGroupName() {
        return this.GroupName != null ? this.GroupName : "" ;
    }
    
    public MonsterInfo getMonsterById(int configId) {
        return MonsterList.stream().filter(m -> m.getID() == configId).findFirst().orElse(null);
    }
    
    @Getter
    public static class GroupProperty {
        private int ID;
        private String Name;
        private String Side; // Enum
        private int DefaultValue;
        private int MaxValue;
    }
    
    public static enum GroupLoadSide {
        Client, Server;
    }

    public static enum GroupCategory {
        Normal, Mission, BattleProps, BattleAudiences, Custom, System, Atmosphere;
    }
}
